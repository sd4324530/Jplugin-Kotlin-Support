package com.haiziwang.platform.jplugin.kotlin

import net.jplugin.core.ctx.ExtensionCtxHelper
import net.jplugin.core.kernel.api.AbstractPlugin
import kotlin.reflect.KClass

/**
 * @author peiyu
 */
class RuleExtension : Extension {

    private val ruleList = ArrayList<Triple<String, KClass<*>, KClass<*>>>()

    override fun register(plugin: AbstractPlugin) {
        ruleList.forEach {
            println("添加rule服务:${it.first}")
            ExtensionCtxHelper.addRuleExtension(plugin, it.first, it.second.java, it.third.java)
        }
    }

    operator fun String.get(interfaceClazz : KClass<*>, implClazz : KClass<*>) = ruleList.add(Triple(this, interfaceClazz, implClazz))
}

inline fun AbstractPlugin.rule(block: RuleExtension.() -> Unit) = RuleExtension().apply(block).register(this)