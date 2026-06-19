package com.sd.laborator


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket


class CensorMicroservice {
    private lateinit var censorMicroserviceServerSocket: ServerSocket

    companion object Constants {
        const val CENSOR_PORT = 1900
    }

    public fun run() {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        censorMicroserviceServerSocket = ServerSocket(CENSOR_PORT)
        println("CensorMicroservice se executa pe portul: ${censorMicroserviceServerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = censorMicroserviceServerSocket.accept()
            println("S-a conectat MessageManager.")

            GlobalScope.launch {
                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                while (true) {
                    var receivedMessage = bufferReader.readLine()

                    if (receivedMessage == null) {
                        bufferReader.close()
                        clientConnection.close()
                        break
                    }

                    println("Primit mesaj: $receivedMessage")

                    val dict_file = File("/home/stefan/Documents/Rezolvari Subiecte/27 Teacher-Student Chat Cenzurat L8/problema 2 clean runnable/CensorMicroservice/src/main/kotlin/com/sd/laborator/dictionar.txt")
                    dict_file.forEachLine {dict_word ->
                        receivedMessage = receivedMessage.replace(
                            dict_word,
                            run {
                                var replacement = ""
                                dict_word.forEach { replacement += "x" }
                                replacement
                            }
                        )
                    }

                    clientConnection.getOutputStream().write((receivedMessage+"\n").toByteArray())
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val censorMicroservice = CensorMicroservice()
    censorMicroservice.run()
}
