package com.sd.laborator
import io.micronaut.rabbitmq.annotation.Queue
import io.micronaut.rabbitmq.annotation.RabbitListener
import jakarta.inject.Inject

@RabbitListener
class QueueListener {

    @Inject
    lateinit var handler: QueueConsumerFunction

    @Queue("eratostene-queue")
    fun receiveMessage(message: QueueMessage)
    {
        handler.accept(message)
    }
}