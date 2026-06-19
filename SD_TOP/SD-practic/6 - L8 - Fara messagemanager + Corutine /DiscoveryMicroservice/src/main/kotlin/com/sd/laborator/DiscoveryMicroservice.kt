package com.sd.laborator

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

data class ParticipantInfo(val name: String, val host: String, val port: Int)

class DiscoveryMicroservice {
    private val activeParticipants = mutableMapOf<String, ParticipantInfo>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        const val DISCOVERY_PORT = 2000
    }

    private fun handleConnection(client: Socket) = scope.launch {
        val reader = BufferedReader(InputStreamReader(client.inputStream))
        val writer = client.getOutputStream()
        try {
            val line = reader.readLine() ?: return@launch
            val parts = line.split(" ", limit = 4)
            val response = when (parts[0]) {
                "REGISTER" -> {
                    val (_, name, host, port) = parts
                    synchronized(activeParticipants) {
                        activeParticipants[name] = ParticipantInfo(name, host, port.toInt())
                    }
                    println("Inregistrat: $name @ $host:$port")
                    "OK\n"
                }
                "UNREGISTER" -> {
                    synchronized(activeParticipants) { activeParticipants.remove(parts[1]) }
                    println("Dezinregistrat: ${parts[1]}")
                    "OK\n"
                }
                "LOOKUP" -> {
                    val info = synchronized(activeParticipants) { activeParticipants[parts[1]] }
                    if (info != null) "FOUND ${info.host} ${info.port}\n" else "NOT_FOUND\n"
                }
                "LIST" -> {
                    val names = synchronized(activeParticipants) {
                        activeParticipants.keys.joinToString(",")
                    }
                    "ACTIVE $names\n"
                }
                else -> "ERROR unknown command\n"
            }
            writer.write(response.toByteArray())
        } finally {
            client.close()
        }
    }

    fun run() {
        val server = ServerSocket(DISCOVERY_PORT)
        println("DiscoveryService pornit pe portul $DISCOVERY_PORT")
        runBlocking {
            while (true) {
                val client = withContext(Dispatchers.IO) { server.accept() }
                handleConnection(client)
            }
        }
    }
}

fun main() {
    DiscoveryMicroservice().run()
}