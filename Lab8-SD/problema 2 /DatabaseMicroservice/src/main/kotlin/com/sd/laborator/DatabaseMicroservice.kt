package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class DatabaseMicroservice {
    private val database: HashMap<Int, MutableList<Double>> = hashMapOf()
    private lateinit var serverSocket: ServerSocket

    companion object {
        const val DATABASE_PORT = 2000
    }

    private fun addNote(studentPort: Int, nota: Double): String {
        synchronized(database) {
            database.getOrPut(studentPort) { mutableListOf() }.add(nota)
            println("Nota $nota adaugata pentru studentul $studentPort")
            return "OK nota $nota adaugata pentru $studentPort"
        }
    }

    private fun finalizeStudent(studentPort: Int): String {
        synchronized(database) {
            val notes = database[studentPort] ?: return "EROARE: studentul $studentPort nu exista in DB"

            if (notes.isEmpty()) {
                return "EROARE: nicio nota pentru studentul $studentPort"
            }

            val medie = notes.average()
            println("Sesiune finalizata pentru $studentPort. Media: $medie")
            return "Media studentului $studentPort: ${"%.2f".format(medie)}"
        }
    }

    private fun getMedia(studentPort: Int): String {
        synchronized(database) {
            val notes = database[studentPort] ?: return "EROARE: studentul $studentPort nu exista"

            if (notes.isEmpty()) {
                return "Nicio nota inca pentru $studentPort"
            }

            val medie = notes.average()
            return "Media curenta $studentPort: ${"%.2f".format(medie)} " + "(note: ${notes.joinToString(", ")})"
        }
    }

    fun run() {
        serverSocket = ServerSocket(DATABASE_PORT)
        println("DatabaseMicroservice pornit pe portul $DATABASE_PORT")

        while (true) {
            val client = serverSocket.accept()
            thread {
                val reader = BufferedReader(InputStreamReader(client.inputStream))

                val message = reader.readLine() ?: return@thread
                println("Primit: $message")

                val parts = message.split(" ", limit = 3)
                val response = when (parts[0]) {
                    // "nota <STUDENT_PORT> <VALOARE>"
                    "nota" -> addNote(
                        parts[1].toInt(),
                        parts[2].toDouble()
                    )
                    // "finalizeaza <STUDENT_PORT>"
                    "finalizeaza" -> finalizeStudent(parts[1].toInt())
                    // "medie <STUDENT_PORT>"
                    "medie" -> getMedia(parts[1].toInt())
                    else -> "EROARE: comanda necunoscuta"
                }

                client.getOutputStream().write("$response\n".toByteArray())
                client.close()
            }
        }
    }
}

fun main() {
    DatabaseMicroservice().run()
}