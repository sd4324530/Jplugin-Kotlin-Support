package com.haiziwang.platform.jplugin.kotlin

import net.jplugin.core.kernel.api.AbstractPlugin
import net.jplugin.core.kernel.api.ExtensionKernelHelper
import net.jplugin.core.kernel.api.IStartup
import kotlin.reflect.KClass

/**
 * @author peiyu
 */
class StartUpExtension : Extension {

    private val startUpList = ArrayList<KClass<out IStartup>>()

    override fun register(plugin: AbstractPlugin) {
        startUpList.forEach {
            println("添加启动扩展:${it.java.name}")
            ExtensionKernelHelper.addStartUpExtension(plugin, it.java)
        }
    }

    operator fun KClass<out IStartup>.unaryPlus() = startUpList.add(this)
}

inline fun AbstractPlugin.startup(block: StartUpExtension.() -> Unit) = StartUpExtension().apply(block).register(this)