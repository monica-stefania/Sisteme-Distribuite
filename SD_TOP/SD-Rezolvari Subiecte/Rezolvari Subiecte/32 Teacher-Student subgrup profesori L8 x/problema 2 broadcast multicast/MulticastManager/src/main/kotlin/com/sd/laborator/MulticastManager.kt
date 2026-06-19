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

class MulticastManager {
    private val subscribers: HashMap<Int, Pair<Socket, String>>
    private lateinit var multicastManagerSocket: ServerSocket

    companion object Constants {
        const val MULTICAST_MANAGER_PORT = 1660
    }

    init {
        subscribers = hashMapOf()
    }

    private fun broadcastMessage(message: String, except: Int) {
        subscribers.forEach {
            it.takeIf { it.key != except && it.value.second != "profesor" }
                ?.value?.first?.getOutputStream()?.write((message + "\n").toByteArray())
        }
    }

    private fun respondTo(message: String) {
        subscribers.forEach { subscriber ->
            if (subscriber.value.second == "profesor") {
                println("Trimit catre profesorul: ${subscriber.key}")
                subscriber.value.first.getOutputStream()?.write((message + "\n").toByteArray())
            }
        }
    }

    public fun run() {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        multicastManagerSocket = ServerSocket(MULTICAST_MANAGER_PORT)
        println("MulticastManager se executa pe portul: ${multicastManagerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            var accepted = 1
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = multicastManagerSocket.accept()


            // se porneste un thread separat pentru tratarea conexiunii cu clientul
            GlobalScope.launch {
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                // asteptam tipul subscriberului
                val subscriber_type = bufferReader.readLine()

                println("Tipul subscriber-ului: $subscriber_type")

                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                synchronized(subscribers) {
                    if(subscriber_type == "student")
                        subscribers[clientConnection.port] = Pair(clientConnection, subscriber_type)
                    else
                        if(subscriber_type == "profesor") {
                            if (Random.nextBoolean()) {
                                subscribers[clientConnection.port] = Pair(clientConnection, subscriber_type)
                                println("Subscriber conectat in grup: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")
                            } else {
                                clientConnection.close()
                                accepted = 0
                            }
                        }
                }
                if (accepted == 1) {
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
                            //bufferReader.close()
                            //clientConnection.close()
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
                                respondTo(messageBody)
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val multicastManager = MulticastManager()
    multicastManager.run()
}
