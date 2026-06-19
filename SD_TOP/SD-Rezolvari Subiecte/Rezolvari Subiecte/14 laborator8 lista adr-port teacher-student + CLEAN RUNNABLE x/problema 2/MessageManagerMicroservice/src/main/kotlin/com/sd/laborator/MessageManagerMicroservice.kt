package com.sd.laborator

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

class MessageManagerMicroservice {
    private val subscribers: HashMap<Int, Socket>
    private lateinit var messageManagerSocket: ServerSocket

    companion object Constants {
        const val MESSAGE_MANAGER_PORT = 1500
    }

    init {
        subscribers = hashMapOf()
    }

    private fun returnStudentsToTeacher(teacherPort: Int) {
        var stringAddressPort = ""
        subscribers.forEach {
            if (it.value.port != teacherPort)
                stringAddressPort += "${it.value.localAddress}:${it.value.port + 4000} " // despartite prin <SPACE>
        }
        println("LISTA SUBSCRIBERI: $subscribers \n cu TEACHERPORT: $teacherPort")
        println("LISTA STUDENTI DIN FUNCTIA returnStudentsToTeacher: $stringAddressPort")
        subscribers[teacherPort]?.getOutputStream()?.write(("$stringAddressPort\n").toByteArray()) //se trimite mesajul cu porturile pe socket-ul teacher-ului
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
            GlobalScope.launch {
                println("Subscriber conectat: ${clientConnection.localAddress}:${clientConnection.port}")
                clientConnection.getOutputStream().write((clientConnection.port.toString() + "\n").toByteArray()) //se trimite portul generat al clientului

                // adaugarea in lista de subscriberi trebuie sa fie atomica!
                synchronized(subscribers) {
                    subscribers[clientConnection.port] = clientConnection
                }

                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                while (true) {
                    // se citeste mesajul de pe socketul TCP
                    var receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa - MENTINE SUBSCRIBERII ACTIVI
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

                    when (receivedMessage) {
                        "intrebare" -> {
                            println("Primit mesaj: $receivedMessage")
                            returnStudentsToTeacher(clientConnection.port)
                        }
                        "raspuns" -> { }
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val messageManagerMicroservice = MessageManagerMicroservice()
    messageManagerMicroservice.run()
}
