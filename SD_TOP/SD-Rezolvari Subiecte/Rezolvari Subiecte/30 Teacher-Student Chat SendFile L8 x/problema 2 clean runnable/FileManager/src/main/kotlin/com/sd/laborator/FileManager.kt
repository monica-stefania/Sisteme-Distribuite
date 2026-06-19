package com.sd.laborator

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class FileManager {
    private val subscribers: HashMap<Int, Socket>
    private lateinit var fileManagerServerSocket: ServerSocket

    companion object Constants {
        const val FILE_MANAGER_PORT = 1900
    }

    init {
        subscribers = hashMapOf()
    }

    private fun broadcastMessage(message: String, except: Int) {
        subscribers.forEach {
            it.takeIf { it.key != except }
                ?.value?.getOutputStream()?.write((message + "\n").toByteArray())
        }
    }

    public fun run() {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        fileManagerServerSocket = ServerSocket(FILE_MANAGER_PORT)
        println("FileManager se executa pe portul: ${fileManagerServerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = fileManagerServerSocket.accept()

            // se porneste un thread separat pentru tratarea conexiunii cu clientul
            GlobalScope.launch {
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                synchronized(subscribers) {
                    subscribers[clientConnection.port] = clientConnection
                }

                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                while (true) {
                    // se citeste raspunsul de pe socketul TCP
                    val receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if (receivedMessage == null) {
                        // deci subscriber-ul respectiv a fost deconectat
                        println("Subscriber-ul ${clientConnection.port} a fost deconectat.")
                        synchronized(subscribers) {
                            subscribers.remove(clientConnection.port)
                        }
                        bufferReader.close()
                        clientConnection.close()
                        break
                    }

                    println("Primit mesaj: $receivedMessage")

                    // "file nume_fisier.txt continut_fisier"
                    val (messageType, file_name, file_content) = receivedMessage.split(" ", limit = 3) // limita 3 pt spatii din continut

                    when (messageType) {
                        "file" -> {
                            broadcastMessage("file $file_name $file_content", except = clientConnection.port)
                        }
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val fileManager = FileManager()
    fileManager.run()
}
