package com.sd.laborator

import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitListener
import jakarta.inject.Inject

@RabbitListener
class ButtonClickListener {
    @Inject
    private lateinit var handler: ButtonClickFunction

    @Queue("click-queue")
    fun execute(buttonName: QueueButton)
    {
        handler.accept(buttonName)
    }
}