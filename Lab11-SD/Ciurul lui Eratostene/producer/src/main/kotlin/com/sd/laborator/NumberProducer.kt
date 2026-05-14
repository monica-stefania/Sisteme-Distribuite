package com.sd.laborator

import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.RabbitClient

@RabbitClient("eratostene-exchange")
interface NumberProducer {
    @Binding("eratostene-queue")
    fun sendNumbers(message: QueueMessage)
}