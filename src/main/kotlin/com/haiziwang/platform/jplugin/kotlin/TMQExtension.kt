package com.haiziwang.platform.jplugin.kotlin

import com.haiziwang.platform.kms.client.consumer.api.ExtensionKmsClientConsumerHelper
import com.haiziwang.platform.kms.client.consumer.api.IKmsConsumerListener
import com.haiziwang.platform.kms.client.producer.api.ExtensionKmsClientProducerHelper
import net.jplugin.core.kernel.api.AbstractPlugin
import kotlin.reflect.KClass

/**
 * TMQ组件扩展
 * @author peiyu
 */
class TMQExtension : Extension {

    internal val producerExtension = ProducerExtension()
    internal val consumerExtension = ConsumerExtension()

    override fun register(plugin: AbstractPlugin) {
        producerExtension.register(plugin)
        consumerExtension.register(plugin)
    }


    inner class ProducerExtension : Extension {

        private val producerList = ArrayList<String>()

        override fun register(plugin: AbstractPlugin) {
            producerList.forEach {
                println("添加TMQ生产者:$it")
                ExtensionKmsClientProducerHelper.addKmsProducerExtension(plugin, it)
            }
        }

        operator fun String.unaryPlus() = producerList.add(this)

    }

    inner class ConsumerExtension : Extension {

        private val listenerList = ArrayList<Pair<String, KClass<out IKmsConsumerListener>>>()

        override fun register(plugin: AbstractPlugin) {
            listenerList.forEach {
                println("添加TMQ消费者,队列:${it.first}")
                ExtensionKmsClientConsumerHelper.addKmsConsumerExtension(plugin, it.first, it.second.java)
            }
        }

        operator fun String.get(clazz: KClass<out IKmsConsumerListener>) = listenerList.add(this to clazz)

    }
}

inline fun AbstractPlugin.tmq(block: TMQExtension.() -> Unit) = TMQExtension().apply(block).register(this)

fun TMQExtension.producer(block: TMQExtension.ProducerExtension.() -> Unit) = producerExtension.block()

fun TMQExtension.consumer(block: TMQExtension.ConsumerExtension.() -> Unit) = consumerExtension.block()