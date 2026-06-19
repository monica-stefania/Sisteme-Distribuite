package com.sd.laborator


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket


class PortFilterMicroservice {
    private lateinit var portFilterMicroserviceServerSocket: ServerSocket

    companion object Constants {
        const val PORT_FILTER_PORT = 1900
    }

    public fun run() {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        portFilterMicroserviceServerSocket = ServerSocket(PORT_FILTER_PORT)
        println("PortFilterMicroservice se executa pe portul: ${portFilterMicroserviceServerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = portFilterMicroserviceServerSocket.accept()
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

                    val results_file = File("/home/stefan/Documents/Rezolvari Subiecte/29 Teacher-Student Chat PortFilter L8 x/problema 2 clean runnable/PortFilterMicroservice/src/main/kotlin/com/sd/laborator/results.txt")
                    if(receivedMessage.toInt() > 30000 && receivedMessage.toInt() < 80000) {
                        results_file.appendText("Conexiune acceptata pe portul $receivedMessage\n")
                        clientConnection.getOutputStream().write(("true"+"\n").toByteArray())
                    }
                    else {
                        results_file.appendText("Conexiune refuzata pe portul $receivedMessage\n")
                        clientConnection.getOutputStream().write(("false"+"\n").toByteArray())
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val portFilterMicroservice = PortFilterMicroservice()
    portFilterMicroservice.run()
}
