package com.haiziwang.platform.jplugin.kotlin

import com.haiziwang.platform.kmem.api.ExtensionKMEMHelper
import net.jplugin.core.kernel.api.AbstractPlugin

/**
 * @author peiyu
 */
class KMemExtension : Extension {

    private val cacheList = ArrayList<String>()

    override fun register(plugin: AbstractPlugin) {
        cacheList.forEach {
            println("添加缓存:$it")
            ExtensionKMEMHelper.addCacheExtension(plugin, it)
        }
    }

    operator fun String.unaryPlus() = cacheList.add(this)
}

inline fun AbstractPlugin.kmem(block: KMemExtension.() -> Unit) = KMemExtension().apply(block).register(this)