package com.haiziwang.platform.jplugin.kotlin

import com.haiziwang.platform.esf.client.cpp.api.ExtensionCppClientHelper
import net.jplugin.core.kernel.api.AbstractPlugin
import kotlin.reflect.KClass

/**
 * @author peiyu
 */

private const val DEFAULT_LENGTH = 4

class CppClientExtension : Extension {

    private val cppClientList = ArrayList<Pair<KClass<*>, Int>>()

    override fun register(plugin: AbstractPlugin) {
        cppClientList.forEach {
            val method = it.first.java.getMethod("getCmdId")
            val cmdId = (method.invoke(it.first.java.newInstance()) as Long).toLong()
            println("添加cpp服务，命令字:$cmdId")
            ExtensionCppClientHelper.addCppClientExtension(plugin, it.first.java, it.second)
        }
    }

    operator fun KClass<*>.unaryPlus() = cppClientList.add(this to DEFAULT_LENGTH)
    operator fun KClass<*>.get(length: Int) = cppClientList.add(this to length)
}

inline fun AbstractPlugin.cppClient(block: CppClientExtension.() -> Unit) = CppClientExtension().apply(block).register(this)