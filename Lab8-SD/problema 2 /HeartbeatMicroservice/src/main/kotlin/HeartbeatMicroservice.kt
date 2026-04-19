package com.sd.laborator

import com.sun.deploy.net.proxy.ProxyInfo
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

        val REGISTRY_HOST = System.getenv("REGISTRY_HOST") ?: "localhost"
        const val REGISTRY_PORT = 1900
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            messageManagerSocket.soTimeout = 0
            messageManagerSocket.getOutputStream().write("identificare Heartbeat $HEARTBEAT_PORT\n".toByteArray())
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }


    private suspend fun sendHeartbeat() = withContext(Dispatchers.IO) {
        while (true) {
            delay(7000) // Pauza de 7 secunde
            try {
                println("\nTrimit mesaj dummy catre retea...")
                val message = "dummy $HEARTBEAT_PORT\n"
                messageManagerSocket.getOutputStream().write("$message\n".toByteArray())
            }catch (e: Exception) {
                println("Eroare la trimitere: ${e.message}")
                break
            }
        }
    }

    private suspend fun listenForDummies() = withContext(Dispatchers.IO) {
        val bufferReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
        println("Astept confirmarile de heartbeat de la MessageManager")
        try {
            while (true) {
                val response = bufferReader.readLine()
                if (response == null) {
                    println("Conexiunea cu MessageManager s-a intrerupt.")
                    break
                }
                println("Am primit mesajul: $response")
                val parts = response.split(" ", limit = 3)
                if (parts.size == 3 && parts[0] == "raspuns") {
                    val serviceStatus = parts[2]
                    println("Confirmat DUMMY: $serviceStatus")
                }
            }
        }catch (e: Exception)
        {
            println("Eroare la citire: ${e.message}")
        }
    }

    fun run() = runBlocking {
        subscribeToMessageManager()

        val listener = launch { listenForDummies() }
        val sender = launch { sendHeartbeat() }

        // Așteptăm ambele corutine (care sunt infinite)
        joinAll(listener, sender)
    }
}

fun main() {
    val heartbeatService = HeartbeatMicroservice()
    heartbeatService.run()
}