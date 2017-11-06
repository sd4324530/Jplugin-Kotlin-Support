package com.haiziwang.platform.jplugin.kotlin.mybatis

/**
 * @author peiyu
 */
open class PageCondition {

    /**
     * @Fields page : 分页查询的信息
     */
    var page: Page? = null

    /**
     * @Fields orderByClause : order by 后面的sql内容
     */
    var orderByClause: String? = null

    constructor()


    constructor(page: Page) {
        this.page = page
    }

    constructor(orderByClause: String) {
        this.orderByClause = orderByClause
    }

    constructor(page: Page, orderByClause: String) {
        this.page = page
        this.orderByClause = orderByClause
    }

    /**
     * @Title: clear
     * @Description: 重置查询条件类
     */
    open fun clear() {
        orderByClause = null
        page = null
    }
}