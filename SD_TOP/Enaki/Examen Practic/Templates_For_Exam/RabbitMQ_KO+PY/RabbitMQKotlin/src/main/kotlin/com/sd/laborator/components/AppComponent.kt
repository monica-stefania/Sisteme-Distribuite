
package com.sd.laborator.components

import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class AppComponent {
    // membrii

    // @Autowired servicii

    @Autowired
    private lateinit var connectionFactory: RabbitMQConnectionFactoryComponent
    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
        this.clearQueue()
    }

    @RabbitListener(queues = ["\${sd.rabbitmq.queue}"])
    fun receiveMessage(msg: String) {
        // mesajul este primit ca un sir de coduri ASCII -> necesita decodare
        val processed_msg = (msg.split(",").map { it.toInt().toChar()}).joinToString(separator="")

        // process message
        print("Mesaj primit: $processed_msg\n")
        sendMessage("Confirmare de la Kotlin al mesajului: $processed_msg")
    }

    fun sendMessage(msg: String) {
        this.amqpTemplate.convertAndSend(connectionFactory.getExchange(), connectionFactory.getRoutingKey(), msg)
        print("[Am trimis un mesaj!]\n")
    }

    fun clearQueue() {
        while(this.amqpTemplate.receive("examen.queue") != null);
        println("Cleared Queue")
    }
    // Other methods
}