package com.haiziwang.platform.jplugin.kotlin.tmq

import com.haiziwang.platform.kms.client.producer.KmsProducerFactory
import com.haiziwang.platform.kms.client.producer.api.IKmsProducer

fun <T> T.tmqProducer(mq: String) : IKmsProducer = KmsProducerFactory.getProducer(mq)
