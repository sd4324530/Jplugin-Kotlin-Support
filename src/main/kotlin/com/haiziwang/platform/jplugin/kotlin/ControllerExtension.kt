package com.haiziwang.platform.jplugin.kotlin

import net.jplugin.core.kernel.api.AbstractPlugin
import net.jplugin.ext.webasic.ExtensionWebHelper
import kotlin.reflect.KClass

/**
 * 控制器扩展，简化定义控制器
 * @author peiyu
 */
class ControllerExtension : Extension {

    private val ctrlList = ArrayList<Pair<String, KClass<*>>>()

    override fun register(plugin: AbstractPlugin) {
        ctrlList.forEach {
            println("添加控制器:${it.first} -> ${it.second}")
            ExtensionWebHelper.addWebExControllerExtension(plugin, it.first, it.second.java)
        }
    }

    operator fun String.get(clazz: KClass<*>) = ctrlList.add(this to clazz)
}

inline fun AbstractPlugin.controller(block: ControllerExtension.() -> Unit) = ControllerExtension().apply(block).register(this)