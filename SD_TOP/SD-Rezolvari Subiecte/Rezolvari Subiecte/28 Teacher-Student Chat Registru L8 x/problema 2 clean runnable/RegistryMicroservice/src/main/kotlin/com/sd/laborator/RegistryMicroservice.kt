package com.sd.laborator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

class Registry {
    val subscriberList: MutableList<Socket>
    private lateinit var registryServerSocket: ServerSocket
    companion object{
        const val REGISTRY_SERVER_PORT = 1900
    }
    init{
        subscriberList = mutableListOf()
    }

    fun run()
    {
        registryServerSocket = ServerSocket(REGISTRY_SERVER_PORT)
        while(true) {
            println("Astept conexiuni de la procesoarele de flux.")
            val clientConnection = registryServerSocket.accept()
            subscriberList.add(clientConnection)
            println("Conexiune acceptata.")
            println("Lista de conexiuni: $subscriberList")

            CoroutineScope(Dispatchers.Default).launch {
                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))
                while (true) {
                    val receivedMessage = bufferReader.readLine()
                    if (receivedMessage == null) {
                        println("${clientConnection.port} s-a fost deconectat.")
                        bufferReader.close()
                        subscriberList.remove(clientConnection)
                        clientConnection.close()
                        break
                    }
                }
            }
        }
    }
}


fun main(args: Array<String>){
    val registry = Registry()
    runBlocking {
        registry.run()
    }
}