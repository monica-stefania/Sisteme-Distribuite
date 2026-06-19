package com.sd.laborator

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import kotlin.concurrent.thread
import kotlin.random.nextInt
import kotlin.system.exitProcess

class TeacherMicroservice(private val teacherPort: Int) {
    private lateinit var messageManagerSocket: Socket
    private lateinit var broadcastManagerSocket: Socket
    private lateinit var multicastManagerSocket: Socket
    private lateinit var teacherMicroserviceServerSocket: ServerSocket

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        val BROADCAST_MANAGER_HOST = System.getenv("BROADCAST_MANAGER_HOST") ?: "localhost"
        val MULTICAST_MANAGER_HOST = System.getenv("MULTICAST_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        const val BROADCAST_MANAGER_PORT = 1650
        const val MULTICAST_MANAGER_PORT = 1660
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            messageManagerSocket.soTimeout = 3000
            messageManagerSocket.getOutputStream().write("profesor\n".toByteArray())
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    private fun subscribeToBroadcastManager() {
        try {
            broadcastManagerSocket = Socket(BROADCAST_MANAGER_HOST, BROADCAST_MANAGER_PORT)
            broadcastManagerSocket.getOutputStream().write("profesor\n".toByteArray())
            println("M-am conectat la BroadcastManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la BroadcastManager!")
            exitProcess(1)
        }
    }

    private fun subscribeToMulticastManager() {
        try {
            multicastManagerSocket = Socket(MULTICAST_MANAGER_HOST, MULTICAST_MANAGER_PORT)
            multicastManagerSocket.getOutputStream().write("profesor\n".toByteArray())
            println("M-am conectat la MulticastManager!")
        } catch (e: Exception) {
            println("Refuzat din MulticastManager!")
            // fara exit process pentru a nu se inchide la refuz
        }
    }

    public fun run() {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToMessageManager()
        subscribeToBroadcastManager()
        subscribeToMulticastManager()

        // se porneste un socket server TCP pe portul 1600 care asculta pentru conexiuni
        teacherMicroserviceServerSocket = ServerSocket(teacherPort)

        println("TeacherMicroservice se executa pe portul: ${teacherMicroserviceServerSocket.localPort}")
        println("Se asteapta cereri (intrebari)...")

        val broadcastManagerBufferReader = BufferedReader(InputStreamReader(broadcastManagerSocket.inputStream))
        val multicastManagerBufferReader = BufferedReader(InputStreamReader(multicastManagerSocket.inputStream))
        while (true) {
            // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
            // (in acest caz, din partea aplicatiei client GUI)
            GlobalScope.launch {
                // se asteapta raspuns de la BroadcastManager
                val receivedResponse = broadcastManagerBufferReader.readLine()
                println("${teacherMicroserviceServerSocket.localPort}: Am primit un raspuns >>> \"$receivedResponse\"")
            }

            GlobalScope.launch {
                // se asteapta raspuns de la MulticastManager
                val receivedResponse = multicastManagerBufferReader.readLine()
                println("${teacherMicroserviceServerSocket.localPort}: Am primit un raspuns de la MULTICAST >>> \"$receivedResponse\"")
            }

            val clientConnection = teacherMicroserviceServerSocket.accept()

            // se foloseste un thread separat pentru tratarea fiecarei conexiuni client
            GlobalScope.launch {
                println("S-a primit o cerere de la: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // se citeste intrebarea dorita
                val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                val (comType, receivedQuestion) = clientBufferReader.readLine().split(" ", limit = 2)

                if(comType.contains("one")) {
                    // intrebarea este redirectionata catre microserviciul MessageManager
                    println("Trimit catre MessageManager: ${"intrebare ${messageManagerSocket.localPort} $receivedQuestion\n"}")
                    messageManagerSocket.getOutputStream()
                        .write(("intrebare ${messageManagerSocket.localPort} $receivedQuestion\n").toByteArray())

                    // se asteapta raspuns de la MessageManager
                    val messageManagerBufferReader =
                        BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
                    try {
                        val receivedResponse = messageManagerBufferReader.readLine()

                        // se trimite raspunsul inapoi clientului apelant
                        println("Am primit raspunsul: \"$receivedResponse\"")
                        clientConnection.getOutputStream().write((receivedResponse + "\n").toByteArray())
                    } catch (e: SocketTimeoutException) {
                        println("Nu a venit niciun raspuns in timp util.")
                        clientConnection.getOutputStream().write("Nu a raspuns nimeni la intrebare\n".toByteArray())
                    } finally {
                        // se inchide conexiunea cu clientul
                        //clientConnection.close()
                    }
                }
                else if(comType.contains("broadcast")) {
                    // intrebarea este redirectionata catre microserviciul MessageManager
                    println("Trimit catre BroadcastManager: ${"intrebare ${broadcastManagerSocket.localPort} $receivedQuestion\n"}")
                    broadcastManagerSocket.getOutputStream().write(("intrebare ${broadcastManagerSocket.localPort} $receivedQuestion\n").toByteArray())
                }
                else if(comType.contains("multicast")) {
                    // intrebarea este redirectionata catre microserviciul MessageManager
                    println("Trimit catre MulticastManager: ${"intrebare ${multicastManagerSocket.localPort} $receivedQuestion\n"}")
                    multicastManagerSocket.getOutputStream().write(("intrebare ${multicastManagerSocket.localPort} $receivedQuestion\n").toByteArray())
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val teacherMicroservice = TeacherMicroservice(args[0].toInt())
    teacherMicroservice.run()
}