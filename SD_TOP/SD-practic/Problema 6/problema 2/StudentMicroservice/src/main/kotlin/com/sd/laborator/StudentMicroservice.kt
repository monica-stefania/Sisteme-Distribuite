package com.sd.laborator

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlin.system.exitProcess

class StudentMicroservice {

    private lateinit var messageManagerSocket: Socket

    companion object {
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        val STUDENT_ID = System.getenv("SERVICE_ID") ?: "student1"
        val DEFAULT_GROUP = System.getenv("GROUP_ID") ?: "grupa1"
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            println("$STUDENT_ID conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    fun run() = runBlocking {
        subscribeToMessageManager()

        val reader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
        val writer = PrintWriter(messageManagerSocket.getOutputStream(), true)

        writer.println(STUDENT_ID)
        writer.println("JOIN|$DEFAULT_GROUP")

        launch(Dispatchers.IO) {
            while (true) {
                val message = reader.readLine() ?: break
                println("Mesaj primit: $message")
            }
        }

        println("Comenzi disponibile:")
        println("PUBLIC|Salut tuturor")
        println("GROUP|$DEFAULT_GROUP|Salut grupa")
        println("PRIVATE|teacher1|Buna ziua")
        println("LEAVE|$DEFAULT_GROUP")

        while (true) {
            val command = readLine() ?: break
            writer.println(command)
        }
    }
}

fun main() {
    StudentMicroservice().run()
}