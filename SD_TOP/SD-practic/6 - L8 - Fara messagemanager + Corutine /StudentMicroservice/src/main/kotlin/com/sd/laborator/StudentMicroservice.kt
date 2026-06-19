package com.sd.laborator

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

class StudentMicroservice {
    private val questionDB: List<Pair<String, String>>
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        val DISCOVERY_HOST = System.getenv("DISCOVERY_HOST") ?: "localhost"
        val MY_NAME = System.getenv("STUDENT_NAME") ?: "student_${(1000..9999).random()}"
        val MY_PORT = (System.getenv("STUDENT_PORT") ?: "3000").toInt()
        const val DISCOVERY_PORT = 2000
    }

    init {
        val lines = File("questions_database.txt").readLines()
        questionDB = lines.chunked(2) { (q, a) -> Pair(q, a) }
    }

    private fun respondToQuestion(question: String): String? =
        questionDB.firstOrNull { it.first == question }?.second

    private suspend fun registerToDiscovery() = withContext(Dispatchers.IO) {
        try {
            Socket(DISCOVERY_HOST, DISCOVERY_PORT).use { sock ->
                sock.getOutputStream().write(
                    "REGISTER $MY_NAME $MY_NAME $MY_PORT\n".toByteArray()
                )
                BufferedReader(InputStreamReader(sock.inputStream)).readLine()
            }
            println("[$MY_NAME] M-am inregistrat la DiscoveryService!")
        } catch (e: Exception) {
            println("[$MY_NAME] Nu ma pot conecta la DiscoveryService!")
            throw e
        }
    }

    private fun handleIncoming(client: Socket) = scope.launch {
        val reader = BufferedReader(InputStreamReader(client.inputStream))
        val writer = client.getOutputStream()
        try {
            val question = reader.readLine() ?: return@launch
            println("[$MY_NAME] Am primit intrebarea: \"$question\"")
            val answer = respondToQuestion(question)
            if (answer != null) {
                println("[$MY_NAME] Trimit raspunsul: \"$answer\"")
                writer.write("$answer\n".toByteArray())
            } else {
                writer.write("NO_ANSWER\n".toByteArray())
            }
        } finally {
            client.close()
        }
    }

    fun run() = runBlocking {
        registerToDiscovery()

        val server = ServerSocket(MY_PORT)
        println("[$MY_NAME] Se asteapta conexiuni pe portul $MY_PORT...")

        while (true) {
            val client = withContext(Dispatchers.IO) { server.accept() }
            println("[$MY_NAME] Conexiune directa de la: " +
                    "${client.inetAddress.hostAddress}:${client.port}")
            handleIncoming(client)
        }
    }
}

fun main() {
    StudentMicroservice().run()
}