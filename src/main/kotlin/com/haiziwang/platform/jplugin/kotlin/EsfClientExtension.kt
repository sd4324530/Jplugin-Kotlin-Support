package com.haiziwang.platform.jplugin.kotlin

import com.haiziwang.platform.esf.client.api.ExtensionESFHelper
import net.jplugin.core.kernel.api.AbstractPlugin
import kotlin.reflect.KClass

/**
 * client代理扩展，简化定义RPC以及restful代理
 * @author peiyu
 */
class EsfClientExtension : Extension {

    internal val rpcProxyExtension = RpcProxyExtension()
    internal val restfulProxyExtension = RestfulProxyExtension()

    override fun register(plugin: AbstractPlugin) {
        rpcProxyExtension.register(plugin)
        restfulProxyExtension.register(plugin)
    }

    /**
     * RPC代理扩展
     * @author peiyu
     */
    inner class RpcProxyExtension : Extension {

        private val rpcProxyList = ArrayList<Pair<String, KClass<*>>>()

        override fun register(plugin: AbstractPlugin) {
            rpcProxyList.forEach {
                println("添加RPC代理:${it.first} -> ${it.second}")
                ExtensionESFHelper.addRPCProxyExtension(plugin, it.second.java, it.first)
            }
        }

        operator fun String.get(clazz: KClass<*>) = rpcProxyList.add(this to clazz)
    }

    /**
     * restful代理扩展
     * @author peiyu
     */
    inner class RestfulProxyExtension : Extension {

        private val restfulProxyList = ArrayList<Pair<String, KClass<*>>>()

        override fun register(plugin: AbstractPlugin) {
            restfulProxyList.forEach {
                println("添加restful代理:${it.first} -> ${it.second}")
                ExtensionESFHelper.addRestfulProxyExtension(plugin, it.second.java, it.first)
            }
        }

        operator fun String.get(clazz: KClass<*>) = restfulProxyList.add(this to clazz)
    }
}

inline fun AbstractPlugin.esfClient(block: EsfClientExtension.() -> Unit) = EsfClientExtension().apply(block).register(this)

fun EsfClientExtension.rpc(block: EsfClientExtension.RpcProxyExtension.() -> Unit) = rpcProxyExtension.block()

fun EsfClientExtension.restful(block: EsfClientExtension.RestfulProxyExtension.() -> Unit) = restfulProxyExtension.block()