package com.haiziwang.platform.jplugin.kotlin.http

import net.jplugin.common.kits.http.HttpKit

/**
 * @author peiyu
 */

inline fun post(url: String, params: Map<String, Any>, headers: Map<String, String> = HashMap(), block: (Pair<String?, Exception?>) -> Unit) {
    val result = try {
        HttpKit.postWithHeader(url, params, headers) to null
    } catch (e: Exception) {
        null to e
    }
    block(result)
}

inline fun get(url: String, headers: Map<String, String> = HashMap(), block: (Pair<String?, Exception?>) -> Unit) {
    val result = try {
        HttpKit.getWithHeader(url, headers) to null
    } catch (e: Exception) {
        null to e
    }
    block(result)
}