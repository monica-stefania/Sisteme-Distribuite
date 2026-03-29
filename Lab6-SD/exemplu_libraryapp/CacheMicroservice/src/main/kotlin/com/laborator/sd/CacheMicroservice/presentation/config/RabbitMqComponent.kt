package com.laborator.sd.CacheMicroservice.presentation.config

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class RabbitMqComponent {
    @Value("\${spring.rabbitmq.host}")
    private lateinit var host: String

    @Value("\${spring.rabbitmq.port}")
    private var port: Int = 0

    @Value("\${spring.rabbitmq.username}")
    private lateinit var username: String

    @Value("\${spring.rabbitmq.password}")
    private lateinit var password: String

    @Value("\${cacheapp.rabbitmq.exchange}")
    private lateinit var exchange: String

    @Value("\${cacheapp.rabbitmq.routingkey.command}")
    private lateinit var routingKeyCommand: String

    @Value("\${cacheapp.rabbitmq.routingkey.file}")
    private lateinit var routingKeyFile: String

    fun getExchange(): String = this.exchange

    fun getRoutingKeyCommand(): String = this.routingKeyCommand
    fun getRoutingKeyFile(): String = this.routingKeyFile

    @Bean
    fun connectionFactory(): ConnectionFactory
    {
        val connectionFactory = CachingConnectionFactory(this.host)
        connectionFactory.port = this.port
        connectionFactory.username = this.username
        connectionFactory.setPassword(password)
        return connectionFactory
    }

    @Bean
    fun rabbitTemplate(): RabbitTemplate
    {
        return RabbitTemplate(connectionFactory())
    }
}