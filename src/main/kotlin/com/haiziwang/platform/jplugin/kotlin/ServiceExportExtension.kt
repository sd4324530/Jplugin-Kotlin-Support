package com.haiziwang.platform.jplugin.kotlin

import net.jplugin.core.kernel.api.AbstractPlugin
import net.jplugin.ext.webasic.ExtensionWebHelper
import kotlin.reflect.KClass

/**
 * @author peiyu
 */
class ServiceExportExtension : Extension {

    private val serviceList = ArrayList<Pair<String, KClass<*>>>()

    override fun register(plugin: AbstractPlugin) {
        serviceList.forEach {
            println("添加对外服务:${it.first} -> ${it.second}")
            ExtensionWebHelper.addServiceExportExtension(plugin, it.first, it.second.java)
        }
    }

    operator fun String.get(clazz: KClass<*>) = serviceList.add(this to clazz)
}

inline fun AbstractPlugin.serviceExport(block: ServiceExportExtension.() -> Unit) = ServiceExportExtension().apply(block).register(this)