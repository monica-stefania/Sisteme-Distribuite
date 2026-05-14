package com.sd.laborator

import io.micronaut.function.FunctionBean
import jakarta.inject.Inject
import java.util.function.Consumer

@FunctionBean("consumer")
class QueueConsumerFunction: Consumer<QueueMessage> {
    @Inject
    lateinit var client: EratosteneClient

    override fun accept(message: QueueMessage) {
        println("Max number received: ${message.maxNumber}")
        println("List of numbers received: ${message.numbersList}")

        val request = EratosteneRequest()
        request.maxNumber = message.maxNumber
        request.numbersToCheck = message.numbersList

        val response = client.calculatePrimes(request)

        println("---------------------------")
        println("Message: ${response.message}")
        println("Primes found: ${response.primes}")
    }
}