package com.haiziwang.platform.jplugin.kotlin

import net.jplugin.core.das.mybatis.api.ExtensionMybatisDasHelper
import net.jplugin.core.kernel.api.AbstractPlugin
import org.apache.ibatis.plugin.Interceptor
import kotlin.reflect.KClass

/**
 * mybatis扩展，简化定义mybatis相关配置
 * @author peiyu
 */
class MybatisExtension : Extension {

    internal val mappingExtension = MappingExtension()
    internal val interceptorExtension = InterceptorExtension()

    override fun register(plugin: AbstractPlugin) {
        ExtensionMybatisDasHelper.addMappingExtension(plugin, "com/haiziwang/platform/jplugin/kotlin/common_SqlMap.xml")
        mappingExtension.register(plugin)
        interceptorExtension.register(plugin)
    }

    /**
     * --------------------------------------------------------------------------------------------------
     */

    /**
     * mapping扩展，简化定义mapping
     * @author peiyu
     */
    inner class MappingExtension : Extension {

        private val mappingList = ArrayList<Pair<String, KClass<*>>>()

        override fun register(plugin: AbstractPlugin) {
            mappingList.forEach {
                if (it.first.isEmpty()) {
                    println("添加mapping:${it.second}")
                    ExtensionMybatisDasHelper.addMappingExtension(plugin, it.second.java)
                } else {
                    println("添加mapping:${it.first} -> ${it.second}")
                    ExtensionMybatisDasHelper.addMappingExtension(plugin, it.first, it.second.java)
                }
            }
        }

        operator fun String.get(clazz: KClass<*>) = mappingList.add(this to clazz)
        operator fun KClass<*>.unaryPlus() = mappingList.add("" to this)
    }


    /**
     * --------------------------------------------------------------------------------------------------
     */

    /**
     * interceptor扩展，简化定义interceptor
     * @author peiyu
     */
    inner class InterceptorExtension : Extension {

        private val interceptorList = ArrayList<Pair<String, KClass<out Interceptor>>>()

        override fun register(plugin: AbstractPlugin) {
            interceptorList.forEach {
                if (it.first.isEmpty()) {
                    println("添加interceptor:${it.second}")
                    ExtensionMybatisDasHelper.addInctprorExtension(plugin, it.second.java)
                } else {
                    println("添加interceptor:${it.first} -> ${it.second}")
                    ExtensionMybatisDasHelper.addInctprorExtension(plugin, it.first, it.second.java)
                }
            }
        }

        operator fun String.get(clazz: KClass<out Interceptor>) = interceptorList.add(this to clazz)
        operator fun KClass<out Interceptor>.unaryPlus() = interceptorList.add("" to this)
    }
}

inline fun AbstractPlugin.mybatis(block: MybatisExtension.() -> Unit) = MybatisExtension().apply(block).register(this)

fun MybatisExtension.mapping(block: MybatisExtension.MappingExtension.() -> Unit) = mappingExtension.block()

fun MybatisExtension.interceptor(block: MybatisExtension.InterceptorExtension.() -> Unit) = interceptorExtension.block()