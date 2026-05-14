package com.sd.laborator

import io.micronaut.rabbitmq.annotation.Binding
import io.micronaut.rabbitmq.annotation.RabbitClient

@RabbitClient("click-exchange")
interface ClickProducer {
    @Binding("click-queue")
    fun sendClick(click: ClickButton)
}