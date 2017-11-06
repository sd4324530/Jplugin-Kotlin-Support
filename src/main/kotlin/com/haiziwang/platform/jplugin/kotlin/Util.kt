package com.haiziwang.platform.jplugin.kotlin

import net.jplugin.core.config.api.ConfigFactory
import net.jplugin.core.das.mybatis.impl.IMybatisService
import net.jplugin.core.log.api.LogFactory
import net.jplugin.core.rclient.proxyfac.ClientProxyFactory
import net.jplugin.core.service.api.ServiceFactory
import kotlin.reflect.KClass

/**
 * @author peiyu
 */
inline fun <reified T> esfClient() = ClientProxyFactory.instance.getClientProxy(T::class.java)

inline fun <T : Any, R> runWithMapper(clazz: KClass<T>, crossinline block: (T) -> R) = ServiceFactory.getService(IMybatisService::class.java).returnWithMapper(clazz.java){block(it)}

fun getStringConfig(path: String) = ConfigFactory.getStringConfig(path)

fun getIntConfig(path: String) = ConfigFactory.getIntConfig(path)

fun getLongConfig(path: String) = ConfigFactory.getLongConfig(path)