package com.haiziwang.platform.jplugin.kotlin.ctrl

import net.jplugin.ext.webasic.api.AbstractExController
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

/**
 * @author peiyu
 */
val AbstractExController.ASC
    get() = " ASC"

val AbstractExController.DESC
    get() = " DESC"

fun AbstractExController.getBooleanParam(param: String) = getParam(param)?.toBoolean() ?: false
fun AbstractExController.getIntParam(param: String) = getParam(param)?.toInt() ?: 0
fun AbstractExController.getLongParam(param: String) = getParam(param)?.toLong() ?: 0L
fun AbstractExController.getFloatParam(param: String) = getParam(param)?.toFloat() ?: 0.0f
fun AbstractExController.getDoubleParam(param: String) = getParam(param)?.toDouble() ?: 0.0
fun AbstractExController.getStringParam(param: String) = getParam(param) ?: ""

val DEFAULT_DATE_FORMAE = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

fun <T : Any> AbstractExController.getBean(clazz: KClass<T>): T {
    val fields = clazz.java.declaredFields
    val params = fields.map {
        when (it.type.kotlin) {
            Boolean::class -> req.getParameter(it.name)?.toBoolean()
            Byte::class -> req.getParameter(it.name)?.toByte()
            Short::class -> req.getParameter(it.name)?.toShort()
            Int::class -> req.getParameter(it.name)?.toInt()
            Long::class -> req.getParameter(it.name)?.toLong()
            Float::class -> req.getParameter(it.name)?.toFloat()
            Double::class -> req.getParameter(it.name)?.toDouble()
            String::class -> req.getParameter(it.name)
            Collection::class -> req.getParameterValues(it.name)?.toList()
            Date::class -> req.getParameter(it.name)?.let(DEFAULT_DATE_FORMAE::parse)
            else -> null
        }
    }.toTypedArray()
    return clazz.java.getConstructor(*fields.map { it.type }.toTypedArray()).newInstance(*params)
}