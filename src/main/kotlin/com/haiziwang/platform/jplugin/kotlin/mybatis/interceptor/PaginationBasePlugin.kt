package com.haiziwang.platform.jplugin.kotlin.mybatis.interceptor

import com.haiziwang.platform.jplugin.kotlin.log.log
import com.haiziwang.platform.jplugin.kotlin.mybatis.Page
import com.haiziwang.platform.jplugin.kotlin.mybatis.PageCondition
import org.apache.ibatis.executor.ErrorContext
import org.apache.ibatis.executor.Executor
import org.apache.ibatis.executor.ExecutorException
import org.apache.ibatis.mapping.BoundSql
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.mapping.ParameterMode
import org.apache.ibatis.mapping.SqlSource
import org.apache.ibatis.plugin.*
import org.apache.ibatis.reflection.property.PropertyTokenizer
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode
import org.apache.ibatis.session.ResultHandler
import org.apache.ibatis.session.RowBounds
import org.apache.ibatis.type.TypeHandler
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*

/**
 * @author peiyu
 */
@Intercepts(Signature(type = Executor::class, method = "query", args = arrayOf(MappedStatement::class, Any::class, RowBounds::class, ResultHandler::class)))
abstract class PaginationBasePlugin : Interceptor {

//    private val log = getLogger(PaginationBasePlugin::class)

    /**
     * @Fields INDEX_MAPPED_STATEMENT : mappStatement参数位置
     */
    internal var INDEX_MAPPED_STATEMENT = 0
    /**
     * @Fields INDEX_PARAMETER : 入参对象参数位置
     */
    internal var INDEX_PARAMETER = 1
    /**
     * @Fields INDEX_ROW_BOUNDS : 记录获取数参数位置
     */
    internal var INDEX_ROW_BOUNDS = 2
    /**
     * @Fields INDEX_RESULT_HANDLER : 结果集处理参数位置
     */
    internal var INDEX_RESULT_HANDLER = 3


    @Throws(Throwable::class)
    override fun intercept(invocation: Invocation): Any {
        log.debug("进入sql分页拦截器.....")
        val mappedStatement = this.getMappedStatement(invocation)
        val parameter = this.getParameter(invocation)
        val rowBounds = getRowBounds(invocation)
        val boundSql = mappedStatement.getBoundSql(parameter)
        // Object[] queryArgs = invocation.getArgs();
        // MappedStatement ms = (MappedStatement) queryArgs[0];
        // BoundSql boundSql = ms.getBoundSql(queryArgs[1]);
        var sql = boundSql.getSql().trim({ it <= ' ' })
        // Object args = queryArgs[1];

        // 分析是否含有分页参数，如果没有则不是分页查询
        // 注意：在多参数的情况下，只处理第一个分页参数
        var condition: PageCondition? = null
        if (parameter is PageCondition) { // 只有一个参数的情况
            condition = parameter
        } else if (parameter is Map<*, *>) { // 多参数的情况，找到第一个Criteria的参数
            for ((_, value) in parameter) {
                if (value is PageCondition) {
                    condition = value
                    break
                }
            }
        }
        val page: Page?
        if (rowBounds.limit == RowBounds.NO_ROW_LIMIT) {
            // 如果基本查询条件为null，则略过继续执行
            if (condition == null) {
                return invocation.proceed()
            } else {
                page = condition.page

                // 如果order by参数不为空，则需要排序后，再分页
                sql = orderbySql(sql, condition.orderByClause ?: "")
            }
        } else {// 如果传入了RowBounds对象，设置RowBounds信息到Page对象中
            page = Page(rowBounds.offset.toLong(), rowBounds.limit.toLong())
        }

        // 如果分页参数不为空，则需要计算记录总数
        if (page != null) {
            val connection = mappedStatement.configuration.environment.dataSource.connection
            val totalCount = getTotalCount(sql, connection, mappedStatement, parameter, boundSql)
            page.count = totalCount.toLong()
            //            PageHolder.setPage(page);
        }

        // 如果page不为空，则生成分页sql
        if (page != null) {
            sql = paginationSql(sql, page) // 通过子类实现
        }
        this.setMappedStatement(invocation, this.buildMappedStatement(mappedStatement, boundSql, sql))
        this.setRowBounds(invocation, RowBounds.DEFAULT)
        return invocation.proceed()
    }


    @Throws(SQLException::class)
    fun getTotalCount(sql: String, connection: Connection?, statement: MappedStatement, parameterObj: Any,
                      boundSql: BoundSql): Int {

        val totalCountSql = totalCountSql(sql)

        var pStatement: PreparedStatement? = null
        var rs: ResultSet? = null

        try {
            pStatement = connection!!.prepareStatement(totalCountSql)

            //        	pStatement = connection.prepareStatement(sql);

            setParameters(pStatement, this.buildMappedStatement(statement, boundSql, totalCountSql), boundSql, parameterObj)
            //        	setParameters(pStatement, this.buildMappedStatement(statement, boundSql, sql), boundSql, parameterObj);
            val start = System.currentTimeMillis()
            rs = pStatement!!.executeQuery()
            val time = System.currentTimeMillis() - start
            log.debug("计算总条数语句:$totalCountSql")
            log.debug("执行耗时:${time}ms")
            if (rs!!.next()) {
                return rs.getInt(1)
            }
        } catch (e: SQLException) {
            throw e
        } finally {
            freeResource(rs, pStatement)
            connection?.close()
        }

        return 0
    }

    /**
     * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
     * @param ps
     * @param mappedStatement
     * @param boundSql
     * @param parameterObject
     * @throws SQLException
     */
    @Throws(SQLException::class)
    private fun setParameters(ps: PreparedStatement?, mappedStatement: MappedStatement, boundSql: BoundSql, parameterObject: Any?) {
        ErrorContext.instance().activity("setting parameters").`object`(mappedStatement.parameterMap.id)
        val parameterMappings = boundSql.parameterMappings
        if (parameterMappings != null) {
            val configuration = mappedStatement.configuration
            val typeHandlerRegistry = configuration.typeHandlerRegistry
            val metaObject = if (parameterObject == null) null else configuration.newMetaObject(parameterObject)
            for (i in parameterMappings.indices) {
                val parameterMapping = parameterMappings[i]
                if (parameterMapping.mode != ParameterMode.OUT) {
                    var value: Any?
                    val propertyName = parameterMapping.property
                    val prop = PropertyTokenizer(propertyName)
                    if (parameterObject == null) {
                        value = null
                    } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.javaClass)) {
                        value = parameterObject
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        value = boundSql.getAdditionalParameter(propertyName)
                    } else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX) && boundSql.hasAdditionalParameter(prop.name)) {
                        value = boundSql.getAdditionalParameter(prop.name)
                        if (value != null) {
                            value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.name.length))
                        }
                    } else {
                        value = metaObject?.getValue(propertyName)
                    }
                    val typeHandler = parameterMapping.typeHandler ?: throw ExecutorException("There was no TypeHandler found for parameter " + propertyName + " of statement "
                            + mappedStatement.id)
                    (typeHandler as TypeHandler<Any>).setParameter(ps, i + 1, value, parameterMapping.jdbcType)

                }
            }
        }
    }

    /**
     * @Title: paginationSql
     * @Description: 子类实现分页sql处理
     * @param sql 原始sql
     * @param page 分页参数zz
     * @return 分页处理后的sql
     */
    protected abstract fun paginationSql(sql: String, page: Page): String  // 子类实现

    /**
     * @Title: orderbySql
     * @Description: 子类实现排序sql处理
     * @param sql 原始sql
     * @param orderByClause 排序参数
     * @return 排序处理后的sql
     */
    protected abstract fun orderbySql(sql: String, orderByClause: String): String  // 子类实现

    /**
     * @Title: totalCountSql
     * @Description: 子类实现总数统计sql处理
     * @param sql 原始sql
     * @return 分页处理后的sql
     */
    protected abstract fun totalCountSql(sql: String): String  // 子类实现

    /**
     * @Title: buildMappedStatement
     * @Description: 生成新的mappedStatement
     * @param ms 原始ms
     * @param boundSql 原始boundsql
     * @param sql 分页最终sql
     * @return 新的mappedStatement
     */
    private fun buildMappedStatement(ms: MappedStatement, boundSql: BoundSql, sql: String): MappedStatement {
        val builder = MappedStatement.Builder(ms.configuration, ms.id, BoundSqlSqlSource(ms, boundSql, sql),
                ms.sqlCommandType)

        builder.resource(ms.resource)
        builder.parameterMap(ms.parameterMap)
        builder.resultMaps(ms.resultMaps)
        builder.fetchSize(ms.fetchSize)
        builder.timeout(ms.timeout)
        builder.statementType(ms.statementType)
        builder.resultSetType(ms.resultSetType)
        builder.cache(ms.cache)
        builder.flushCacheRequired(ms.isFlushCacheRequired)
        builder.useCache(ms.isUseCache)
        builder.keyGenerator(ms.keyGenerator)
        builder.keyProperty(delimitedArraytoString(ms.keyProperties))
        builder.keyColumn(delimitedArraytoString(ms.keyColumns))
        builder.databaseId(ms.databaseId)

        return builder.build()
    }

    /**
     * @Title: getMappedStatement
     * @Description: 获取 MappedStatement
     * @param invocation 调用
     * @return MappedStatement
     */
    private fun getMappedStatement(invocation: Invocation): MappedStatement {
        return invocation.args[INDEX_MAPPED_STATEMENT] as MappedStatement
    }

    /**
     * @Title: setMappedStatement
     * @Description: 设置MappedStatement
     * @param invocation 调用
     * @param mappedStatement 新的MappedStatement
     */
    private fun setMappedStatement(invocation: Invocation, mappedStatement: MappedStatement) {
        invocation.args[INDEX_MAPPED_STATEMENT] = mappedStatement
    }

    /**
     * @Title: getParameter
     * @Description: 获取sql入参
     * @param invocation 调用
     * @return sql入参
     */
    private fun getParameter(invocation: Invocation): Any {
        return invocation.args[INDEX_PARAMETER]
    }

    /**
     * @Title: getRowBounds
     * @Description: 获取行数
     * @param invocation 调用
     * @return 行数范围
     */
    private fun getRowBounds(invocation: Invocation): RowBounds {
        return invocation.args[INDEX_ROW_BOUNDS] as RowBounds
    }

    /**
     * @Title: setRowBounds
     * @Description: 设置行数范围
     * @param invocation 调用
     * @param rowBounds 行数范围
     */
    private fun setRowBounds(invocation: Invocation, rowBounds: RowBounds) {
        invocation.args[INDEX_ROW_BOUNDS] = rowBounds
    }

    override fun plugin(target: Any): Any {
        return Plugin.wrap(target, this)
    }

    /**
     * @Title: delimitedArraytoString
     * @Description: 将参数转string
     * @param in properties
     * @return keyProperty
     */
    private fun delimitedArraytoString(`in`: Array<String>?): String? {
        if (`in` == null || `in`.size == 0) {
            return null
        } else {
            val answer = StringBuffer()
            for (str in `in`) {
                answer.append(str).append(",")
            }
            return answer.toString()
        }
    }

    /**
     * @ClassName: BoundSqlSqlSource
     * @Description: boundsql处理类
     * @author linyl linyuliang.85@gmail.com
     */
    class BoundSqlSqlSource
    /**
     *
     *
     * Title: 构造函数
     *
     *
     *
     * Description:根据新的ms和boundsql生成sqlsource
     *
     * @param ms 新的ms
     * @param boundSql 新的boundsql
     * @param sql 执行sql
     */
    (ms: MappedStatement, boundSql: BoundSql, sql: String) : SqlSource {

        /**
         * @Fields boundSql : 新boundsql
         */
        private val boundSql: BoundSql

        init {
            this.boundSql = buildBoundSql(ms, boundSql, sql)
        }

        override fun getBoundSql(parameterObject: Any): BoundSql {
            return boundSql
        }

        /**
         * @Title: buildBoundSql
         * @Description: 入参处理
         * @param ms 新的ms
         * @param boundSql 新的boundsql
         * @param sql 执行sql
         * @return 最终boundsql
         */
        private fun buildBoundSql(ms: MappedStatement, boundSql: BoundSql, sql: String): BoundSql {
            val newBoundSql = BoundSql(ms.configuration, sql, boundSql.parameterMappings, boundSql.parameterObject)
            for (mapping in boundSql.parameterMappings) {
                val prop = mapping.property
                if (boundSql.hasAdditionalParameter(prop)) {
                    newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop))
                }
            }
            return newBoundSql
        }
    }

    override fun setProperties(properties: Properties) {
        // String dialectClass = properties.getProperty("dialect");
        // try {
        // dialect = (Dialect) Class.forName(dialectClass).newInstance();
        // } catch (Exception e) {
        // throw new MyBatisShardsException("Can not create dialect instance by dialect:"
        // + dialectClass, e);
        // }
    }

    /**
     * free the database resource
     * @param rs
     * @param ps
     */
    fun freeResource(rs: ResultSet?, ps: PreparedStatement?) {
        try {
            rs?.close()
            ps?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }
}