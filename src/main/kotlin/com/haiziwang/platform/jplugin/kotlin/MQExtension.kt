package com.haiziwang.platform.jplugin.kotlin

import com.haiziwang.platform.mq.api.KmqExtensionHelper
import net.jplugin.core.kernel.api.AbstractPlugin
import javax.jms.MessageListener
import kotlin.reflect.KClass

/**
 * @author peiyu
 */
class MQExtension : Extension {

    private val listenerList = ArrayList<Pair<String, KClass<out MessageListener>>>()

    override fun register(plugin: AbstractPlugin) {
        listenerList.forEach {
            println("添加mq监听器:${it.first}")
            KmqExtensionHelper.addMessageListenerExtension(plugin, it.first, it.second.java)
        }
    }

    operator fun String.get(clazz: KClass<out MessageListener>) = listenerList.add(this to clazz)
}

inline fun AbstractPlugin.mq(block: MQExtension.() -> Unit) = MQExtension().apply(block).register(this)