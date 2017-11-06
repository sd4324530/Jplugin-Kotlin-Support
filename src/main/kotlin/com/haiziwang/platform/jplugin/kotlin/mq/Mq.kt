package com.haiziwang.platform.jplugin.kotlin.mq

import com.haiziwang.platform.mq.api.KmqFactory
import com.haiziwang.platform.mq.producer.JmsProducer

/**
 * @author peiyu
 */

fun <T> T.jmsProducer(mq: String) : JmsProducer? = KmqFactory.getInstance().getJmsProducer(mq)