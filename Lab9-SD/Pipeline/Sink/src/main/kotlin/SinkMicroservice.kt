package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink

@EnableBinding(Sink::class)
@SpringBootApplication
open class SinkMicroservice {
    @StreamListener(Sink.INPUT)
    fun finalResult(message: String) {
        println("=== I received the message === ")
        println(message)
        println()
    }
}

fun main(args: Array<String>)
{
    runApplication<SinkMicroservice>(*args)
}