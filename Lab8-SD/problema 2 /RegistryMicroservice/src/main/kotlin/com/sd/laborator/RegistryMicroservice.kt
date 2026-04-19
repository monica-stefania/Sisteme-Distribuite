package com.sd.laborator

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import kotlin.concurrent.thread

data class ServiceEntry(val name: String, val port: Int)

class RegistryMicroservice {
    private val registry: HashMap<String, ServiceEntry> = hashMapOf()
    private lateinit var registrySocket: ServerSocket

    companion object {
        const val REGISTRY_PORT = 1900
    }

    fun run(){
        registrySocket = ServerSocket(REGISTRY_PORT)
        println("com.sd.laborator.RegistryMicroservice pornit pe portul $REGISTRY_PORT")

        while(true){
            val clientConnection = registrySocket.accept()
            thread{
                val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                val message = bufferReader.readLine()
                val parts = message.split(" ", limit = 4)

                when(parts[0])
                {
                    "subscribe" -> {
                        val entry = ServiceEntry(parts[1], parts[2].toInt())
                        synchronized(registry){
                            registry[parts[1]] = entry
                        }
                        clientConnection.getOutputStream().write(("subscribed ${parts[1]}\n").toByteArray())
                        println("Subscribed: ${parts[1]} -> ${parts[2]}")
                    }
                    "unsubscribe" ->
                    {
                        synchronized(registry){
                            registry.remove(parts[1])
                        }
                        clientConnection.getOutputStream().write(("unsubscribed ${parts[1]}\n").toByteArray())
                        println("Unsubscribed: ${parts[1]}")
                    }
                    "list" -> {
                        val response = registry.values.joinToString(";"){
                            "${it.name}, ${it.port}"
                        }
                        clientConnection.getOutputStream().write(("$response\n").toByteArray())
                    }
                }
                clientConnection.close()
            }
        }
    }
}

fun main(args: Array<String>){
    val registryMicroservice = RegistryMicroservice()
    registryMicroservice.run()
}