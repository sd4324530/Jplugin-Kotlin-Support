package com.haiziwang.platform.jplugin.kotlin.mybatis

import net.jplugin.ext.webasic.api.AbstractExController
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @author peiyu
 */
class Criteria : PageCondition {
    /**
     * @Fields attrs : 额外的键值对参数，不建议使用，请注意mybatis的sql写法，将sql入参写成方法的入参，这样dao的api可查
     */
    @Deprecated("")
    var attrs: MutableMap<String, Any>

    /**
     * @Fields distinct : 是否要distinct
     */
    var distinct: Boolean = false

    /**
     * @Fields locale : 国际化标识 zh_CN,或en等
     */
    var locale: String? = null

    /**
     * @Fields oredCriteria : 查询条件对象的组合链
     */
    val oredCriteria: MutableList<Condition>

    constructor(): super() {
        this.oredCriteria = ArrayList()
        this.attrs = HashMap()
    }

    constructor(page: Page): super(page) {
        this.oredCriteria = ArrayList()
        this.attrs = HashMap()
    }

    fun or(condition: Condition) = oredCriteria.add(condition)

    fun or() : Condition {
        val condition = createConditionInternal()
        oredCriteria.add(condition)
        return condition
    }

    /**
     * @Title: createConditon
     * @Description: 创建查询条件对象
     * @return 一个新的查询条件对象
     */
    fun createConditon(): Condition {
        val condition = createConditionInternal()
        if (oredCriteria.isEmpty()) {
            oredCriteria.add(condition)
        }
        return condition
    }

    /**
     * @Title: createConditionInternal
     * @Description: 创建一个查询条件对象
     * @return 新建的查询条件对象
     */
    private fun createConditionInternal(): Condition {
        return Condition()
    }

    /**
     * @Title: clear
     * @Description: 重置查询条件类
     */
    override fun clear() {
        super.clear()
        oredCriteria.clear()
        attrs.clear()
        distinct = false
        locale = null
    }


    class Condition {
        /**
         * @Fields LIKE : sql语句中的like
         */
        private val LIKE = " like"
        /**
         * @Fields " not like" : sql语句中的not like
         */
        private val NOT_LIKE = " not like"

        /**
         * @Fields ANY_MATCH : sql语句中的%号通配符
         */
        private val ANY_MATCH = "%"

        /**
         * @Fields UPPER_LEFT : sql语句中大写化方法左边
         */
        private val UPPER_LEFT = "upper("

        /**
         * @Fields UPPER_RIGHT: sql语句中大写化方法右边
         */
        private val UPPER_RIGHT = ") like"

        /**
         * @Fields criterions : 原子查询条件
         */
        var criterions: MutableList<Criterion> = ArrayList()

        fun isValid(): Boolean = criterions.isNotEmpty()

        fun addCriterion(condition: String) = criterions.add(Criterion(condition))

        fun addCriterion(condition: String, value: Any) = criterions.add(Criterion(condition, value))
        fun addCriterion(condition: String, value: Any, property: String) = criterions.add(Criterion(condition, value))

        fun addCriterion(condition: String, value1: Any, value2: Any) = criterions.add(Criterion(condition, value1, value2))
        fun addCriterion(condition: String, value1: Any, value2: Any, property: String) = criterions.add(Criterion(condition, value1, value2))

        fun andIsNull(columnName: String): Condition {
            addCriterion(columnName + " is null")
            return this
        }

        fun andIsNotNull(columnName: String): Condition {
            addCriterion(columnName + " is not null")
            return this
        }

        fun andEqualTo(columnName: String, value: Any): Condition {
            addCriterion(columnName + " =", value, columnName)
            return this
        }

        /**
         * @Title: andNotEqualTo
         * @Description: and某个字段非指定值
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andNotEqualTo(columnName: String, value: Any): Condition {
            addCriterion(columnName + " <>", value, columnName)
            return this
        }

        /**
         * @Title: andGreaterThan
         * @Description: and某个字段大于指定值
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andGreaterThan(columnName: String, value: Any): Condition {
            addCriterion(columnName + " >", value, columnName)
            return this
        }

        /**
         * @Title: andGreaterThanOrEqualTo
         * @Description: and某个字段大于等于指定值
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andGreaterThanOrEqualTo(columnName: String, value: Any): Condition {
            addCriterion(columnName + " >=", value, columnName)
            return this
        }

        /**
         * @Title: andLessThan
         * @Description: and某个字段小于指定值
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andLessThan(columnName: String, value: Any): Condition {
            addCriterion(columnName + " <", value, columnName)
            return this
        }

        /**
         * @Title: andLessThanOrEqualTo
         * @Description: and某个字段小于等于指定值
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andLessThanOrEqualTo(columnName: String, value: Any): Condition {
            addCriterion(columnName + " <=", value, columnName)
            return this
        }

        /**
         * @Title: andBetween
         * @Description: and 某个字段在v1和v2的范围内
         * @param columnName 字段名
         * @param value1 值1
         * @param value2 值2
         * @return 增加条件后的条件类对象
         */
        fun andBetween(columnName: String, value1: Any, value2: Any): Condition {
            addCriterion(columnName + " between", value1, value2)
            return this
        }

        /**
         * @Title: andNotBetween
         * @Description: and 某个字段不在v1和v2的范围内
         * @param columnName 字段名
         * @param value1 值1
         * @param value2 值2
         * @return 增加条件后的条件类对象
         */
        fun andNotBetween(columnName: String, value1: Any, value2: Any): Condition {
            addCriterion(columnName + " not between", value1, value2)
            return this
        }

        /**
         * @Title: andIn
         * @Description: and 某个字段在列表中
         * @param columnName 字段名
         * @param values 列表
         * @return 增加条件后的条件类对象
         */
        fun andIn(columnName: String, values: List<Any>?): Condition {
            if (values != null && values.size > 0) {
                addCriterion(columnName + " in", values, columnName)
            } else {
                addCriterion("1 != 1")
            }
            return this
        }

        /**
         * @Title: andNotIn
         * @Description: and 某个字段不在列表中
         * @param columnName 字段名
         * @param values 列表
         * @return 增加条件后的条件类对象
         */
        fun andNotIn(columnName: String, values: List<Any>?): Condition {
            if (values != null && values.size > 0) {
                addCriterion(columnName + " not in", values, columnName)
            }
            return this
        }

        /**
         * @Title: andLeftLike
         * @Description: and 某字段左边匹配
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andLeftLike(columnName: String, value: String): Condition {
            if (value.isNotEmpty()) {
                addCriterion(columnName + LIKE, value + ANY_MATCH, columnName)
            }
            return this
        }

        /**
         * @Title: andRightLike
         * @Description: and 某字段右边匹配
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andRightLike(columnName: String, value: String): Condition {
            if (value.isNotEmpty()) {
                addCriterion(columnName + LIKE, ANY_MATCH + value, columnName)
            }
            return this
        }

        /**
         * @Title: andLike
         * @Description: and 某字段任意包含指定值
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andLike(columnName: String, value: String): Condition {
            if (value.isNotEmpty()) {
                addCriterion(columnName + LIKE, ANY_MATCH + value + ANY_MATCH, columnName)
            }
            return this
        }

        /**
         * @Title: andLeftNotLike
         * @Description: and 某字段左边不以 指定值开始
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andLeftNotLike(columnName: String, value: String): Condition {
            if (value.isNotEmpty()) {
                addCriterion(columnName + NOT_LIKE, value + ANY_MATCH, columnName)
            }
            return this
        }

        /**
         * @Title: andLeftNotLike
         * @Description: and 某字段右边不以 指定值结束
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andRightNotLike(columnName: String, value: String): Condition {
            if (value.isNotEmpty()) {
                addCriterion(columnName + NOT_LIKE, ANY_MATCH + value, columnName)
            }
            return this
        }

        /**
         * @Title: andNotLike
         * @Description: and 某字段不包含指定值
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andNotLike(columnName: String, value: String): Condition {
            if (value.isNotEmpty()) {
                addCriterion(columnName + NOT_LIKE, ANY_MATCH + value + ANY_MATCH, columnName)
            }
            return this
        }

        /**
         * @Title: andLikeInsensitive
         * @Description: and 某字段不区分大小写包含指定值
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andLikeInsensitive(columnName: String, value: String): Condition {
            if (value.isNotEmpty()) {
                addCriterion(UPPER_LEFT + columnName + UPPER_RIGHT, ANY_MATCH + value.toUpperCase() + ANY_MATCH, columnName)
            }
            return this
        }

        /**
         * @Title: andLeftLikeInsensitive
         * @Description: and 某字段左边不区分大小写匹配指定值
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andLeftLikeInsensitive(columnName: String, value: String): Condition {
            if (value.isNotEmpty()) {
                addCriterion(UPPER_LEFT + columnName + UPPER_RIGHT, value.toUpperCase() + ANY_MATCH, columnName)
            }
            return this
        }

        /**
         * @Title: andRightLikeInsensitive
         * @Description: and 某字段右边不区分大小写匹配指定值
         * @param columnName 字段名
         * @param value 值
         * @return 增加条件后的条件类对象
         */
        fun andRightLikeInsensitive(columnName: String, value: String): Condition {
            if (value.isNotEmpty()) {
                addCriterion(UPPER_LEFT + columnName + UPPER_RIGHT, ANY_MATCH + value.toUpperCase(), columnName)
            }
            return this
        }
    }


    class Criterion {
        /**
         * @Fields condition : 判断条件
         */
        val condition: String

        /**
         * @Fields value :第一个参数值
         */
        var value: Any? = null
            private set

        /**
         * @Fields secondValue : 第2个参数值
         */
        var secondValue: Any? = null
            private set

        /**
         * @Fields noValue : 无需参数
         */
        var noValue: Boolean = false
            private set

        /**
         * @Fields singleValue :只有一个参数
         */
        var singleValue: Boolean = false
            private set

        /**
         * @Fields betweenValue : 是否是between两个参数
         */
        var betweenValue: Boolean = false
            private set

        /**
         * @Fields listValue : 是否参数是列表值
         */
        var listValue: Boolean = false
            private set

        /**
         * @Fields dateValue : 是否参数是日期类型
         */
        var dateValue: Boolean = false
            private set

        /**
         * @Fields typeHandler : 类型转换处理
         */
        val typeHandler: String?

        constructor(condition: String) {
            this.condition = condition
            this.typeHandler = null
            this.noValue = true
        }

        constructor(condition: String, value: Any, typeHandler: String?) {
            this.condition = condition
            this.value = value
            this.typeHandler = typeHandler
            when (value) {
                is List<*> -> this.listValue = true
                is Date -> this.dateValue = true
                else -> this.singleValue = true
            }
        }

        constructor(condition: String, value: Any) : this(condition, value, null)

        constructor(condition: String, value: Any, secondValue: Any, typeHandler: String?) {
            this.condition = condition
            this.value = value
            this.secondValue = secondValue
            this.typeHandler = typeHandler
            this.betweenValue = true
        }

        constructor(condition: String, value: Any, secondValue: Any) : this(condition, value, secondValue, null)
    }
}

fun AbstractExController.createCriteria() : Criteria {
    val page = getParam("page")
    return if (null == page || page.isEmpty()) {
        Criteria()
    } else {
        val begin = 10 * (page.toLong() - 1)
        val pageSize = getParam("pageSize")
        if (null == pageSize || pageSize.isEmpty())
            Criteria(Page(begin, 10L))
        else
            Criteria(Page(begin, pageSize.toLong()))
    }
}