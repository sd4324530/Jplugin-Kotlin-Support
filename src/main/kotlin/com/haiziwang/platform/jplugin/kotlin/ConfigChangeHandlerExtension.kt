package com.haiziwang.platform.jplugin.kotlin

import net.jplugin.core.config.ExtensionConfigHelper
import net.jplugin.core.config.api.IConfigChangeHandler
import net.jplugin.core.kernel.api.AbstractPlugin
import kotlin.reflect.KClass

class ConfigChangeHandlerExtension : Extension {

    private val handlerList = ArrayList<Pair<String, KClass<out IConfigChangeHandler>>>()

    override fun register(plugin: AbstractPlugin) {
        handlerList.forEach {
            println("添加配置变更监听:${it.first} -> ${it.second.java.name}")
            ExtensionConfigHelper.addConfigChangeHandlerExtension(plugin, it.first, it.second.java)
        }
    }

    operator fun String.get(clazz: KClass<out IConfigChangeHandler>) = handlerList.add(this to clazz)
}

inline fun AbstractPlugin.configChangeHandler(block : ConfigChangeHandlerExtension.() -> Unit) = ConfigChangeHandlerExtension().apply(block).register(this)