package com.sd.laborator

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlin.system.exitProcess

class TeacherMicroservice {

    private lateinit var messageManagerSocket: Socket

    companion object Constants {
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        val TEACHER_ID = System.getenv("SERVICE_ID") ?: "teacher1"
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            println("Teacher conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    fun run() = runBlocking {
        subscribeToMessageManager()

        val reader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
        val writer = PrintWriter(messageManagerSocket.getOutputStream(), true)

        writer.println(TEACHER_ID)

        launch(Dispatchers.IO) {
            while (true) {
                val message = reader.readLine() ?: break
                println("Mesaj primit: $message")
            }
        }

        println("Comenzi disponibile:")
        println("PUBLIC|Salut tuturor")
        println("GROUP|grupa1|Mesaj pentru grupa1")
        println("PRIVATE|student1|Mesaj privat")

        while (true) {
            val command = readLine() ?: break
            writer.println(command)
        }
    }
}

fun main() {
    TeacherMicroservice().run()
}