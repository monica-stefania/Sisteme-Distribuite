package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import java.io.BufferedReader
import java.util.concurrent.TimeUnit

@EnableBinding(Processor::class)
@SpringBootApplication
open class ProcessorMicroservice {

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    fun processCommand(message: String): String {
        println("Processor received: $message")

        if(!message.contains('#'))
            return message

        val messageParts = message.split("#", limit = 2)
        val input = messageParts[0]
        val commandString = messageParts.getOrElse(1) { "" }.trim()

        if (commandString.isEmpty()) {
            return message
        }

        val commandParts = commandString.split("|", limit = 2)
        val firstCommand = commandParts[0].trim()
        val remainingCommands = if (commandParts.size > 1) commandParts[1].trim() else ""

        val result = executeCommand(firstCommand, input)

        return if (remainingCommands.isEmpty()) {
            println("Se trimite mesajul: " + result)
            result
        } else {
            println("Se trimite mesajul: " + "$result#$remainingCommands")
            "$result#$remainingCommands"
        }



        /*
        println("Received pipeline: $message")

        val commands = message.split("|").map { it.trim() }

        var currentOutput = ""

        for (cmd in commands) {
            println("Executing: $cmd")

            currentOutput = executeCommand(cmd, currentOutput)

            println("Intermediate result:\n$currentOutput")
        }

        println("Pipeline finished.")
        return currentOutput
        */

    }

    private fun executeCommand(command: String, input: String): String
    {
        return try {
            val process = ProcessBuilder("bash", "-c", command)
                .redirectErrorStream(true)
                .start()

            // Pipe input
            if (input.isNotEmpty()) {
                process.outputStream.use { it.write(input.toByteArray()); it.flush() }
            }
            process.outputStream.close()

            // read output
            val output = process.inputStream.bufferedReader().use(BufferedReader::readText)
            val error = process.errorStream.bufferedReader().use(BufferedReader::readText)

            process.waitFor(5, TimeUnit.SECONDS)
            println("Exec $command -> output: $output, error: $error")
            output.trim()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}

fun main(args: Array<String>)
{
    runApplication<ProcessorMicroservice>(*args)
}