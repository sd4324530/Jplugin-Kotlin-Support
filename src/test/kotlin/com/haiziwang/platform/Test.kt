package com.haiziwang.platform

/**
 * @author peiyu
 */
fun main(args: Array<String>) {
    val map = mapOf("id" to 1, "name" to "test")
    val fields = User::class.java.declaredFields
    val args = fields.map { map[it.name] }.toTypedArray()
    val user = User::class.java.getConstructor(*fields.map { it.type }.toTypedArray()).newInstance(*args)
    println(user)
}

data class User(var id: Int, var name: String)