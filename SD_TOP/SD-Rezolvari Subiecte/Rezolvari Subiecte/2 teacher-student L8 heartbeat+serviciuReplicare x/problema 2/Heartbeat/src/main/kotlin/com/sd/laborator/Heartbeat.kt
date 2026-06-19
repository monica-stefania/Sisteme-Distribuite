package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.RuntimeException
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class Heartbeat{

    private lateinit var messageManagerSocket: Socket

    companion object Constants{
        val MESSAGE_MANAGER_HOST = System.getenv("MESSAGE_MANAGER_HOST") ?: "localhost"
        const val MESSAGE_MANAGER_PORT = 1500
    }

    private fun subscribeToMessageManager(){
        try{
            messageManagerSocket = Socket(MESSAGE_MANAGER_HOST, MESSAGE_MANAGER_PORT)
            messageManagerSocket.soTimeout = 3000 //pt a nu sta mai mult de 3 sec socket-ul
            println("M-am conectat la Message Manager!")
        }catch(e: Exception){
            println("Nu m-am putut conecta la Message Manager!")
            exitProcess(1)
        }
    }

    fun run(){
        subscribeToMessageManager()
        println("Se testeaza...")
        while(true){
            try{
                messageManagerSocket.getOutputStream().write(("ping\n").toByteArray())
                val messageManagerBufferedReader = BufferedReader(InputStreamReader(messageManagerSocket.inputStream))
                val receivedResponse = messageManagerBufferedReader.readLine()
                println("Am primit raspunsul: \"$receivedResponse\"")
            }catch (e:Exception){
                println(e.message)
                println("Nu a venit niciun raspuns in timp util.")

                //Runtime.getRuntime().exec("java -jar F:/ManagerMicroservice/MessageManagerMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar")

                val serviceCallerSocket = Socket("localhost", 1777)
                serviceCallerSocket.getOutputStream().write("message_manager\n".toByteArray())
                serviceCallerSocket.close()

                println("Relansare MessageManager")
                Thread.sleep(3000)
                subscribeToMessageManager()
            }
            Thread.sleep(1000)
        }
    }
}

fun main(args: Array<String>){
    val heartbeat = Heartbeat()
    heartbeat.run()
}