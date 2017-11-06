package com.haiziwang.platform.jplugin.kotlin

import net.jplugin.core.kernel.api.AbstractPlugin

/**
 * 扩展接口，表示jplugin框架的一个扩展点功能
 * @author peiyu
 */
interface Extension {

    /**
     * 注册这个扩展
     * @param plugin 需要注册到的plugin
     */
    fun register(plugin: AbstractPlugin)
}

/**
 * 扩展异常
 * @author peiyu
 */
class ExtensionException(override val message: String = "") : RuntimeException()