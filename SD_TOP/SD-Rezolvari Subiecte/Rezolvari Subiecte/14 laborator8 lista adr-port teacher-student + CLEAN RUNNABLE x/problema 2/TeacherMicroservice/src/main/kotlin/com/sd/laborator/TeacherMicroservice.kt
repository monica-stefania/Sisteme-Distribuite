package com.sd.laborator

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class TeacherMicroservice {
    private lateinit var messageManagerSocket: Socket
    private lateinit var teacherMicroserviceServerSocket: ServerSocket

    companion object Constants {
        // pentru testare, se foloseste localhost. pentru deploy, server-ul socket (microserviciul MessageManager) se identifica dupa un "hostname"
        // acest hostname poate fi trimis (optional) ca variabila de mediu
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
        const val TEACHER_PORT = 1600
    }

    private fun subscribeToMessageManager() {
        try {
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            messageManagerSocket.soTimeout = 3000
            println("M-am conectat la MessageManager!")
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageManager!")
            exitProcess(1)
        }
    }

    public fun run() {
        // microserviciul se inscrie in lista de "subscribers" de la MessageManager prin conectarea la acesta
        subscribeToMessageManager()
        var reader = BufferedReader(InputStreamReader(messageManagerSocket.getInputStream()))
        val port = reader.readLine().toInt() //citeste primul mesaj fara a-l folosi

        // se porneste un socket server TCP pe portul 1600 care asculta pentru conexiuni
        teacherMicroserviceServerSocket = ServerSocket(TEACHER_PORT)

        println("TeacherMicroservice se executa pe portul: ${teacherMicroserviceServerSocket.localPort}")
        println("Se asteapta cereri (intrebari)...")

        while (true) {
            // se asteapta conexiuni din partea clientilor ce doresc sa puna o intrebare
            // (in acest caz, din partea aplicatiei client GUI)
            val clientConnection = teacherMicroserviceServerSocket.accept()

            // se foloseste un thread separat pentru tratarea fiecarei conexiuni client
            GlobalScope.launch {
                println("S-a primit o cerere de la: ${clientConnection.inetAddress.hostAddress}:${clientConnection.port}")

                // se citeste intrebarea dorita
                val clientBufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                val receivedQuestion = clientBufferReader.readLine()
                // S-a citit intrebarea (metoda blocanta)

                // cerere lista socketuri studenti catre microserviciul MessageManager
                println("Trimit o cerere catre MessageManager pentru a lua lista de studenti")
                messageManagerSocket.getOutputStream().write("intrebare\n".toByteArray())

                // se asteapta raspuns de la MessageManager
                val teacherMicroserviceBufferReader = BufferedReader(InputStreamReader(messageManagerSocket.getInputStream()))
                try {
                    val studentMap = mutableMapOf<Int, String>()
                    teacherMicroserviceBufferReader.readLine().removeSuffix(" ").split(' ').forEach { perecheAdrPort->
                        val adresa = perecheAdrPort.split(':')[0]
                        val port = perecheAdrPort.split(':')[1]
                        studentMap[port.toInt()]=adresa
                    }
                    println("LISTA PRIMITA: $studentMap")
                    // se trimite intrebarea tuturor studentilor
                    studentMap.forEach {
                        GlobalScope.launch {
                            println("Se creeaza socket cu: " + it.value.removePrefix("/") + " - " + it.key)
                            val socketStudent = Socket(it.value.removePrefix("/"), it.key) // adresa si port
                            socketStudent.getOutputStream().write((receivedQuestion + "\n").toByteArray()) // intrebarea primita din GUI
                            val studentReader = BufferedReader(InputStreamReader(socketStudent.getInputStream()))
                            val response = studentReader.readLine()
                            if(response != "nu stiu") {
                                clientConnection.getOutputStream().write((response + "\n").toByteArray())
                            }
                            //socketStudent.close()
                        }
                    }
                }
                catch (e: Exception) {}
                finally {
                    // se inchide conexiunea cu clientul
                    //clientConnection.close()
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    val teacherMicroservice = TeacherMicroservice()
    teacherMicroservice.run()
}