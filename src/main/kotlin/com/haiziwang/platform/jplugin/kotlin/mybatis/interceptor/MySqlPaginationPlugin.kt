package com.haiziwang.platform.jplugin.kotlin.mybatis.interceptor

import com.haiziwang.platform.jplugin.kotlin.mybatis.Page
import org.apache.ibatis.executor.Executor
import org.apache.ibatis.mapping.MappedStatement
import org.apache.ibatis.plugin.Intercepts
import org.apache.ibatis.plugin.Signature
import org.apache.ibatis.session.ResultHandler
import org.apache.ibatis.session.RowBounds

/**
 * @author peiyu
 */
@Intercepts(Signature(type = Executor::class, method = "query", args = arrayOf(MappedStatement::class, Any::class, RowBounds::class, ResultHandler::class)))
class MySqlPaginationPlugin : PaginationBasePlugin() {

    override fun paginationSql(sql: String, page: Page) = "$sql limit ${page.begin},${page.length}"

    override fun orderbySql(sql: String, orderByClause: String) = if (orderByClause.isEmpty()) sql else "$sql order by $orderByClause"

    override fun totalCountSql(sql: String) = "select count(1) from ($sql) as total_count"
}