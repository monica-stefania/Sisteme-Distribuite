package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.InboundChannelAdapter
import org.springframework.integration.annotation.Poller
import org.springframework.integration.dsl.Pollers.fixedDelay
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import java.io.File

@EnableBinding(Source::class)
@SpringBootApplication
open class SourceMicroservice {

    private var allCommands = mutableListOf<String>()
    private var currentIndex = 0

    init{
        try{
            allCommands = File("/home/moni/Documents/Sisteme-Distribuite/Lab9-SD/Pipeline/commands.txt").useLines { it.toMutableList() }

        }catch(e: Exception)
        {
            println("Error from reading the file!")
        }
    }

    @Bean
    @InboundChannelAdapter(value = Source.OUTPUT, poller = [Poller(fixedDelay = "5000", maxMessagesPerPoll = "1")])
    open fun sendCommand(): () -> Message<String>? {
        return {
            if (currentIndex < allCommands.size) {
                val command = allCommands[currentIndex]
                currentIndex++

                println("Sending the pipeline: #$command")
                MessageBuilder.withPayload("#$command").build()
            } else {
                null
            }
        }
    }
}

fun main(args: Array<String>)
{
    runApplication<SourceMicroservice>(*args)
}