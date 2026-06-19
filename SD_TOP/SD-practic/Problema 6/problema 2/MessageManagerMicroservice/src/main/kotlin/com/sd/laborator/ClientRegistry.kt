package com.sd.laborator

import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

class ClientRegistry {
    private val clients = ConcurrentHashMap<String, Socket>()

    fun register(clientId: String, socket: Socket) {
        clients[clientId] = socket
        println("Client registered: $clientId")
    }

    fun unregister(clientId: String) {
        clients.remove(clientId)
        println("Client removed: $clientId")
    }

    fun getClient(clientId: String): Socket? {
        return clients[clientId]
    }

    fun allClients(): Map<String, Socket> {
        return clients.toMap()
    }
}