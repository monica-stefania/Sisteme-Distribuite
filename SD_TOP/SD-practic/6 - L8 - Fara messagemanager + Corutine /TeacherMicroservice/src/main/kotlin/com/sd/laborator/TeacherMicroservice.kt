package com.sd.laborator

import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException

class TeacherMicroservice {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        val DISCOVERY_HOST = System.getenv("DISCOVERY_HOST") ?: "localhost"
        const val DISCOVERY_PORT = 2000
        const val TEACHER_PORT = 1600
        const val TIMEOUT_MS = 3000
    }

    private suspend fun registerToDiscovery() = withContext(Dispatchers.IO) {
        try {
            Socket(DISCOVERY_HOST, DISCOVERY_PORT).use { sock ->
                sock.getOutputStream().write(
                    "REGISTER teacher teacher $TEACHER_PORT\n".toByteArray()
                )
                BufferedReader(InputStreamReader(sock.inputStream)).readLine()
            }
            println("[Teacher] M-am inregistrat la DiscoveryService!")
        } catch (e: Exception) {
            println("[Teacher] Nu ma pot conecta la DiscoveryService!")
            throw e
        }
    }

    // Cere lista de studenti activi direct de la DiscoveryService
    private suspend fun listActiveStudents(): List<String> = withContext(Dispatchers.IO) {
        try {
            Socket(DISCOVERY_HOST, DISCOVERY_PORT).use { sock ->
                sock.getOutputStream().write("LIST\n".toByteArray())
                val response = BufferedReader(
                    InputStreamReader(sock.inputStream)
                ).readLine() ?: return@withContext emptyList()

                if (response.startsWith("ACTIVE")) {
                    val names = response.removePrefix("ACTIVE ").split(",")
                    names.filter { it.isNotBlank() && it != "teacher" }
                } else emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Afla adresa si portul unui participant de la DiscoveryService
    private suspend fun lookupParticipant(name: String): Pair<String, Int>? =
        withContext(Dispatchers.IO) {
            try {
                Socket(DISCOVERY_HOST, DISCOVERY_PORT).use { sock ->
                    sock.getOutputStream().write("LOOKUP $name\n".toByteArray())
                    val response = BufferedReader(
                        InputStreamReader(sock.inputStream)
                    ).readLine() ?: return@withContext null

                    if (response.startsWith("FOUND")) {
                        val parts = response.split(" ")
                        Pair(parts[1], parts[2].toInt())
                    } else null
                }
            } catch (e: Exception) {
                null
            }
        }

    // Comunicare P2P directa cu un student — fara intermediar
    private suspend fun askStudentDirectly(host: String, port: Int, question: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val sock = Socket(host, port)
                sock.soTimeout = TIMEOUT_MS
                sock.getOutputStream().write("$question\n".toByteArray())
                val answer = BufferedReader(InputStreamReader(sock.inputStream)).readLine()
                sock.close()
                if (answer == null || answer == "NO_ANSWER") null else answer
            } catch (e: SocketTimeoutException) {
                null
            } catch (e: Exception) {
                null
            }
        }

    // Intreaba toti studentii activi in paralel cu corutine, returneaza primul raspuns
    private suspend fun askQuestion(question: String): String {
        val students = listActiveStudents()
        if (students.isEmpty()) return "Nu exista studenti activi."

        println("[Teacher] Studenti activi: $students")

        // async/awaitAll = echivalentul thread-urilor din lab, dar cu corutine
        val deferreds = students.mapNotNull { name ->
            val info = lookupParticipant(name) ?: return@mapNotNull null
            val (host, port) = info
            scope.async {
                println("[Teacher] Intreb direct pe $name @ $host:$port")
                askStudentDirectly(host, port, question)
            }
        }

        val results = deferreds.awaitAll()
        return results.firstOrNull { it != null }
            ?: "Nu a raspuns nimeni la intrebare."
    }

    private fun handleClient(client: Socket) = scope.launch {
        val reader = BufferedReader(InputStreamReader(client.inputStream))
        val writer = client.getOutputStream()
        try {
            val question = reader.readLine() ?: return@launch
            println("[Teacher] Intrebare primita: \"$question\"")
            val answer = askQuestion(question)
            println("[Teacher] Raspuns final: \"$answer\"")
            writer.write("$answer\n".toByteArray())
        } finally {
            client.close()
        }
    }

    fun run() = runBlocking {
        registerToDiscovery()

        val server = ServerSocket(TEACHER_PORT)
        println("[Teacher] Se asteapta intrebari pe portul $TEACHER_PORT...")

        while (true) {
            val client = withContext(Dispatchers.IO) { server.accept() }
            println("[Teacher] Client conectat: " +
                    "${client.inetAddress.hostAddress}:${client.port}")
            handleClient(client)
        }
    }
}

fun main() {
    TeacherMicroservice().run()
}