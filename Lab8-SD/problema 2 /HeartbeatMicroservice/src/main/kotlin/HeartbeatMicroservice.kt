package com.sd.laborator

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import kotlin.system.exitProcess

class HeartbeatMicroservice {
    private lateinit var messageManagerSocket: Socket

    companion object Constants {
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        const val HEARTBEAT_PORT= 1800
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            messageManagerSocket.getOutputStream().write("identificare $HEARTBEAT_PORT\n".toByteArray())
            println("Heartbeat: M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Heartbeat: Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    private suspend fun sendHeartbeat() = withContext(Dispatchers.IO) {
        while (true) {
            delay(5000) // Pauza de 5 secunde
            try {
                println("\n[Heartbeat] Trimit mesaj dummy catre retea...")
                val message = "dummy $HEARTBEAT_PORT \n"
                messageManagerSocket.getOutputStream().write("$message\n".toByteArray())
            }catch (e: Exception) {
                println("Heartbeat: Eroare la trimitere: ${e.message}")
                break
            }
        }
    }

    private suspend fun listenForDummies() = withContext(Dispatchers.IO) {
        val bufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))

        while (true) {
            val response = bufferReader.readLine()
            if (response == null) {
                println("[Heartbeat] Conexiunea cu MessageManager s-a intrerupt.")
                break
            }

            val parts = response.split(" ", limit = 3)
            if (parts.size == 3 && parts[0] == "raspuns") {
                val serviceStatus = parts[2]
                println("Confirmat DUMMY: $serviceStatus")
            }
        }
    }

    fun run() = runBlocking {
        subscribeToMessageManager()

        launch { listenForDummies() }
        launch { sendHeartbeat() }
    }
}

fun main() {
    val heartbeatService = HeartbeatMicroservice()
    heartbeatService.run()
}