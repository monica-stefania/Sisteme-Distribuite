package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

class MessageManagerMicroservice {
    private val subscribers: HashMap<Int, Socket>
    private val subscriberNames: HashMap<Int, String> = hashMapOf()
    private val socketToPort: HashMap<Int, Int> = hashMapOf()

    private lateinit var messageManagerSocket: ServerSocket
    private lateinit var registrySocket: Socket

    companion object Constants {
        const val MESSAGE_MANAGER_PORT = 1500
        const val REGISTRY_PORT = 1900
        val REGISTRY_HOST = System.getenv("REGISTRY_HOST") ?: "localhost"
    }

    init {
        subscribers = hashMapOf()
    }

    private fun broadcastMessage(message: String, except: Int) {
        println("BROADCAST de la portul $except => $message")
        subscribers.forEach { (port, socket) ->
            // Trimitem catre toti, EXCEPTAND expeditorul si porturile virtuale de UI (>10000)
            if (port != except && port < 10000) {
                println("Trimit copie catre portul $port")
                try {
                    socket.getOutputStream().write((message + "\n").toByteArray())
                } catch (e: Exception) {
                    println("Eroare la trimiterea catre $port")
                }
            }
        }
    }

    private fun respondTo(destination: Int, message: String) {
        val destinationSocket = subscribers[destination]

        if (destinationSocket != null) {
            println("Trimit mesaj către portul $destination: $message")
            destinationSocket.getOutputStream().write((message + "\n").toByteArray())
        } else {
            println("Eroare: Destinatarul cu portul $destination nu a fost gasit in catalog!")
        }
    }

    private fun notifyRegistry(action: String, serviceName: String, servicePort: Int){
        try{
            registrySocket = Socket(REGISTRY_HOST, REGISTRY_PORT)
            val message = when(action) {
                "subscribe" -> "subscribe $serviceName $servicePort"
                "unsubscribe" -> "unsubscribe $serviceName"
                else -> return
            }

            registrySocket.getOutputStream().write("$message\n".toByteArray())

            val response = BufferedReader(InputStreamReader(registrySocket.inputStream)).readLine()
            println("Registry a raspuns: $response")
            registrySocket.close()
        }catch (e: Exception) {
            println("Nu pot notifica Registry: ${e.message}")
        }
    }

    public fun run() {
        // se porneste un socket server TCP pe portul 1500 care asculta pentru conexiuni
        messageManagerSocket = ServerSocket(MESSAGE_MANAGER_PORT)
        println("MessageManagerMicroservice se executa pe portul: ${messageManagerSocket.localPort}")
        println("Se asteapta conexiuni si mesaje...")

        while (true) {
            // se asteapta conexiuni din partea clientilor subscriberi
            val clientConnection = messageManagerSocket.accept()

            // se porneste un thread separat pentru tratarea conexiunii cu clientul
            thread {
                println("Subscriber conectat: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                /*synchronized(subscribers) {
                    subscribers[clientConnection.port] = clientConnection
                }

                 */

                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                while (true) {
                    // se citeste raspunsul de pe socketul TCP
                    val receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if (receivedMessage == null) {
                        // deci subscriber-ul respectiv a fost deconectat
                        println("Subscriber-ul ${clientConnection.port} a fost deconectat.")
                        val port = socketToPort[clientConnection.port]
                        val name = subscriberNames[port] ?: "unknown"
                        synchronized(subscribers) {
                            subscribers.remove(clientConnection.port)
                            subscriberNames.remove(clientConnection.port)
                        }
                        notifyRegistry("unsubscribe", name, port!!)
                        bufferReader.close()
                        clientConnection.close()
                        break
                    }

                    println("Primit mesaj: $receivedMessage")
                    val messageType = receivedMessage.substringBefore(" ")

                    when (messageType) {
                        "intrebare" -> {
                            val parts = receivedMessage.split(" ", limit = 4)
                            val senderPort = parts[1].toInt()
                            val destPort = parts[2]
                            val text = parts[3]
                            val messageForTarget = "intrebare $senderPort $text"

                            if(destPort == "ALL")
                                broadcastMessage(messageForTarget, senderPort)
                            else
                                respondTo(destPort.toInt(), messageForTarget)
                        }
                        "raspuns" -> {
                            val parts = receivedMessage.split(" ", limit = 3)
                            val destPort = parts[1].toInt()
                            val text = parts[2]

                            respondTo(destPort, text)
                        }

                        "identificare" -> {
                            val parts = receivedMessage.split(" ", limit = 3)
                            val serviceName = parts[1]
                            val appPort = parts[2].toInt()
                            subscribers[appPort] = clientConnection
                            subscriberNames[appPort] = serviceName
                            socketToPort[clientConnection.port] = appPort

                            println("S-a inregistrat in catalog clientul $serviceName cu portul: $appPort")
                            notifyRegistry("subscribe", serviceName, appPort)
                        }

                        "dummy" -> {
                            val parts = receivedMessage.split(" ", limit = 2)
                            val senderPort = parts[1].toInt()
                            broadcastMessage(receivedMessage, senderPort)
                        }
                    }
                }
            }
        }
    }
}

fun main() {
    val messageManagerMicroservice = MessageManagerMicroservice()
    messageManagerMicroservice.run()
}
