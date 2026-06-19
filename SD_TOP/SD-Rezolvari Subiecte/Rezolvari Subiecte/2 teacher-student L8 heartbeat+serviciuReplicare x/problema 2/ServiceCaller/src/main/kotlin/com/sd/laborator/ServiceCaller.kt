package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.RuntimeException
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class ServiceCaller{

    private lateinit var serviceCallerServerSocket: ServerSocket

    companion object Constants{
        const val SERVICE_CALLER_PORT = 1777
    }


    fun run() {
        serviceCallerServerSocket = ServerSocket(1777)

        while (true) {
            try {
                println("Astept conexiunea cu heartbeat.")
                val heartbeatConnection = serviceCallerServerSocket.accept()
                println("Am facut conexiunea cu heartbeat.")

                val serviceCallerBufferedReader = BufferedReader(InputStreamReader(heartbeatConnection.inputStream))
                val request = serviceCallerBufferedReader.readLine()
                println("Request de replicare: \"$request\"")

                if (request.contains("message_manager"))
                    Runtime.getRuntime()
                        .exec("java -jar /home/stefan/Documents/MessageManagerMicroservice-1.0-SNAPSHOT-jar-with-dependencies.jar")
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}

fun main(args: Array<String>){
    val serviceCaller = ServiceCaller()
    serviceCaller.run()
}