package com.haiziwang.platform.jplugin.kotlin.mybatis

/**
 * @author peiyu
 */
class Page {

    constructor(begin: Long, length: Long) {
        this.begin = begin
        this.length = length
        this.end = this.begin + this.length
        this.current = if (this.length != 0L) {
            Math.floor(this.begin * 1.0 / this.length).toLong() + 1
        } else {
            1
        }
    }

    constructor(begin: Long, length: Long, count: Long) : this(begin, length) {
        this.count = count
    }

    /**
     * @Fields begin : 分页查询开始记录位置
     */
    var begin: Long = 0
        set(value) {
            field = value
            this.current = if (this.length > 0) {
                (Math.floor(field * 1.0 / this.length).toInt() + 1).toLong()
            } else {
                1
            }
        }

    /**
     * @Fields end : 分页查看下结束位置
     */
    var end: Long = 0
    /**
     * @Fields length : 每页显示记录数
     */
    var length: Long = 0
        set(value) {
            field = value
            if (this.begin != 0L) {
                this.current = if (this.length > 0) {
                    (Math.floor(this.begin * 1.0 / field).toInt() + 1).toLong()
                } else {
                    1
                }
            }
        }

    /**
     * @Fields count : 查询结果总记录数
     */
    var count: Long = 0
        set(value) {
            field = value
            if (this.length > 0) {
                this.total = Math.floor(field * 1.0 / this.length).toLong()
                if (field % this.length != 0L) {
                    this.total += 1
                }
            } else {
                this.total = 1
            }
        }

    /**
     * @Fields current : 当前页码
     */
    var current: Long = 0

    /**
     * @Fields total : 总共页数
     */
    var total: Long = 0

    var needCount = true

}