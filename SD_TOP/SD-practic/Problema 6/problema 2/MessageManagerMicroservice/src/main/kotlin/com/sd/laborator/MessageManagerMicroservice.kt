package com.sd.laborator

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.net.ServerSocket
import java.net.Socket

class MessageManagerMicroservice {
    private val clientRegistry = ClientRegistry()
    private val groupService = GroupService()
    private val streamProcessor = StreamProcessor(clientRegistry, groupService)

    private val messageChannel = Channel<ChatMessage>(Channel.UNLIMITED)

    fun run() = runBlocking {
        val server = ServerSocket(1500)

        println("MessageManagerMicroservice started on port 1500")

        launch {
            for (message in messageChannel) {
                streamProcessor.process(message)
            }
        }

        while (true) {
            println("Astept client...")
            val socket = server.accept()
            println("Socket acceptat!")

            launch(Dispatchers.IO) {
                handleClient(socket)
            }
        }
    }

    private suspend fun handleClient(socket: Socket) {
        println("Am intrat in handleClient")

        val reader = socket.getInputStream().bufferedReader()

        println("Astept clientId...")
        val clientId = reader.readLine()
        println("Client conectat: $clientId")

        try {
            while (true) {
                val line = reader.readLine() ?: break
                val message = parseMessage(clientId, line)
                println(message)
                messageChannel.send(message)
            }
        } finally {
            clientRegistry.unregister(clientId)
            socket.close()
        }
    }

    private fun parseMessage(senderId: String, line: String): ChatMessage {
        val parts = line.split("|", limit = 3)
        println("Parsez: $line")
        return when (parts[0]) {
            "PRIVATE" -> ChatMessage(
                senderId,
                MessageType.PRIVATE,
                parts.getOrNull(1),
                parts.getOrNull(2) ?: ""
            )

            "GROUP" -> ChatMessage(
                senderId,
                MessageType.GROUP,
                parts.getOrNull(1),
                parts.getOrNull(2) ?: ""
            )

            "PUBLIC" -> ChatMessage(
                senderId,
                MessageType.PUBLIC,
                null,
                parts.getOrNull(1) ?: ""
            )

            "JOIN" -> ChatMessage(
                senderId,
                MessageType.JOIN,
                parts.getOrNull(1),
                ""
            )

            "LEAVE" -> ChatMessage(
                senderId,
                MessageType.LEAVE,
                parts.getOrNull(1),
                ""
            )

            else -> ChatMessage(
                senderId,
                MessageType.PUBLIC,
                null,
                line
            )
        }
    }
}

fun main() {
    MessageManagerMicroservice().run()
}