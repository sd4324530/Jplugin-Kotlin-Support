package com.haiziwang.platform.jplugin.kotlin.log

import net.jplugin.core.log.api.LogFactory
import net.jplugin.core.log.api.Logger
import org.apache.log4j.Level

/**
 * @author peiyu
 */

class LogWarper(private val log: Logger) {

    fun debug(msg: String) = log.debug(msg)

    fun debug(block: () -> String) {
        if(log.isDebugEnabled)
            log.debug(block())
    }

    fun info(msg: String) = log.info(msg)

    fun info(block: () -> String) {
        if (log.isInfoEnabled)
            log.info(block())
    }

    fun warn(msg: String, throwable: Throwable? = null) = log.warn(msg as Any, throwable)

    fun warn(throwable: Throwable? = null, block: () -> String) {
        if (log.isEnabledFor(Level.WARN))
            log.warn(block() as Any, throwable)
    }

    fun error(msg: String, throwable: Throwable? = null) = log.error(msg as Any, throwable)

    fun error(throwable: Throwable? = null, block: () -> String) {
        if (log.isEnabledFor(Level.ERROR))
            log.error(block() as Any, throwable)
    }
}

inline val <T: Any> T.log
    get() = LogWarper(LogFactory.getLogger(this::class.java))