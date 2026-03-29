package com.laborator.sd.CacheMicroservice.presentation.controllers

import com.laborator.sd.CacheMicroservice.business.interfaces.ICachingService
import com.laborator.sd.CacheMicroservice.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class CachingController {
    @Autowired
    private lateinit var _cachingService: ICachingService

    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent
    private lateinit var _ampqTemplate: AmqpTemplate

    @Autowired
    fun initTemplate()
    {
        this._ampqTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    //preiau mesajul din coada query -> verific daca acest query da hit sau miss
    @RabbitListener(queues = ["\${cacheapp.rabbitmq.queue.query}"])
    fun fetchQuery(message: String)
    {
        print("Query primit: $message")
        val cache = _cachingService.exists(message)
        if (cache != null)
        {
            //HIT - exista si timestamp-ul nu a depasit o ora
            sendFile(cache.cacheResult)
        }
        else
        {
            //MISS sau HIT, dar cu timestamp-ul depasit de o ora
            sendCommand(message)
        }
    }

    @RabbitListener(queues = ["\${cacheapp.rabbitmq.queue.state}"])
    fun fetchState(message: String)
    {
        print("The state from LibraryApp: $message")
        val parts = message.split("~", limit = 2)
        if (parts.size == 2)
        {
            val query = parts[0]
            val result = parts[1]

            _cachingService.addToCache(query, result)
            sendFile(result)
        }
    }

    fun sendCommand(message: String)
    {
        println("Command to send: $message")
        this._ampqTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyCommand(), message)
    }
    fun sendFile(message: String)
    {
        println("File to send: $message")
        this._ampqTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyFile(), message)
    }
}