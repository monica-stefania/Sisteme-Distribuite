package com.sd.laborator

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import kotlin.concurrent.thread

class CensorMicroservice{
    private lateinit var censorMicroserviceServer: ServerSocket
    private lateinit var dictionary: List<String>

    companion object Constants {
        const val CENSOR_PORT = 1700
    }

    fun censorText(text: String): String {
        val words = text.split(" ")
        val processedWords = words.map { word ->
            val cleanWord = word.lowercase().replace(Regex("[^a-zA-Z0-9ăâîșțĂÂÎȘȚ]"), "")

            if (dictionary.contains(cleanWord)) {
                "x".repeat(word.length)
            } else {
                word
            }
        }
        return processedWords.joinToString(" ")
    }
    fun run()
    {
        censorMicroserviceServer = ServerSocket(CENSOR_PORT)
        dictionary = File("dictionar.txt").readLines()

        println("CensorMicroservice se executa pe portul: ${censorMicroserviceServer.localPort}")
        println("Se asteapta cereri ...")

        while(true)
        {
            val clientConnection = censorMicroserviceServer.accept()

            thread {
                // se citeste intrebarea dorita
                val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                val outputStream = clientConnection.getOutputStream()

                try {
                    while(true) {
                        val receivedMessage = clientBufferReader.readLine()

                        println("Am primit mesajul: \"$receivedMessage\"")

                        val censoredMessage = censorText(receivedMessage)

                        println("Trimit înapoi textul curățat: \"$censoredMessage\"")

                        outputStream.write((censoredMessage + "\n").toByteArray())
                        outputStream.flush()
                    }
                }
                catch(e: Exception)
                {
                    println("Eroare la conexiunea cu clientul")
                }
                finally {
                    clientConnection.close()
                }
            }
        }
    }
}

fun main() {
    val censorMicroservice = CensorMicroservice()
    censorMicroservice.run()
}