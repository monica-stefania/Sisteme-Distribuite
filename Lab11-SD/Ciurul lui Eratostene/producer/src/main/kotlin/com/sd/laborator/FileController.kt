package com.sd.laborator

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import java.io.File

@Controller("/produce")
class FileController(
    private val numberProducer: NumberProducer
) {

    @Get("/send/{maxNumber}")
    fun readAndSend(maxNumber: Int): String
    {
        val file = File("numbers.txt")

        if(!file.exists())
        {
            return "File not found!"
        }
        val numbersList = file.readLines().map { it.toInt() }

        val messageToSend = QueueMessage(maxNumber, numbersList)

        numberProducer.sendNumbers(messageToSend)
        return "Successfully sent ${numbersList.size} numbers"
    }
}