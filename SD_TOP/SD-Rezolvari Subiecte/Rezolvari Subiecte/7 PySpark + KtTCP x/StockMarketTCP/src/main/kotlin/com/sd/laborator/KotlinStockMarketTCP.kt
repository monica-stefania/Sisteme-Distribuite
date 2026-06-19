package com.sd.laborator
import com.sun.security.ntlm.Client
import kotlinx.serialization.json.*
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.system.exitProcess


class GetterTCP {
    private fun sendToPython(client:Socket, data: String) {
        try {
            client.getOutputStream().write((data+"\n").toByteArray())
        } catch (e: Exception) {
            println("Nu ma pot conecta la Python!")
            exitProcess(1)
        }
    }

    fun sendGet() {
        val server = ServerSocket(9999)
        println("Server is running on port ${server.localPort}")
        val client = server.accept()
        println("Client connected: ${client.inetAddress.hostAddress}")

        val r = khttp.get("https://finnhub.io/api/v1/stock/symbol?exchange=US&token=brl7eb7rh5re1lvco7fg")

        val rJson = Json.parse(JsonArraySerializer, r.text)
        for (it in rJson)
        {
            println(it.jsonObject["symbol"])
            val symbolDetails = khttp.get("https://finnhub.io/api/v1/stock/price-target?symbol=" + it.jsonObject["symbol"].toString().removeSurrounding("\"") + "&token=brl7eb7rh5re1lvco7fg")

            val dataToSend = symbolDetails.text
            println(dataToSend)

            sendToPython(client, dataToSend)
            println("______________________")
            sleep(3000)
        }
    }
}

fun main(args: Array<String>) {
    val getTCP = GetterTCP()
    getTCP.sendGet()
}