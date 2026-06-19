package com.sd.laborator

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.random.Random

class GroupManager {
    private val subscribers: HashMap<Int, Socket>
    private lateinit var groupManagerServerSocket: ServerSocket

    companion object Constants {
        const val GROUP_MANAGER_PORT = 1900
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

    private fun respondTo(destination: Int, message: String) {
        subscribers[destination]?.getOutputStream()?.write((message + "\n").toByteArray())
    }

    public fun run() {
        groupManagerServerSocket = ServerSocket(GROUP_MANAGER_PORT)
        println("GroupManager se executa pe portul: ${groupManagerServerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            var accepted = 1
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = groupManagerServerSocket.accept()

            // se porneste un thread separat pentru tratarea conexiunii cu clientul
            GlobalScope.launch {
                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                synchronized(subscribers) {
                    if (Random.nextBoolean()) {
                        subscribers[clientConnection.port] = clientConnection
                        println("Subscriber conectat in grup: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")
                    }
                    else {
                        clientConnection.close()
                        accepted = 0
                    }
                }

                if (accepted == 1) {
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
                        val (messageType, messageDestination, messageBody) = receivedMessage.split(" ", limit = 3)

                        when (messageType) {
                            "intrebare" -> {
                                // tipul mesajului de tip intrebare este de forma:
                                // intrebare <DESTINATIE_RASPUNS> <CONTINUT_INTREBARE>
                                broadcastMessage(
                                    "intrebare ${clientConnection.port} $messageBody",
                                    except = clientConnection.port
                                )
                            }
                            "raspuns" -> {
                                // tipul mesajului de tip raspuns este de forma:
                                // raspuns <CONTINUT_RASPUNS>
                                respondTo(messageDestination.toInt(), messageBody)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val groupManager = GroupManager()
    groupManager.run()
}
