package com.sd.laborator
import com.sun.security.ntlm.Client
import java.util.Calendar.DAY_OF_MONTH
import kotlinx.serialization.json.*
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.Calendar.MONTH
import java.util.Calendar.YEAR
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

        val r = khttp.get("https://finnhub.io/api/v1/stock/symbol?exchange=US&token=brmrfu7rh5rcss140ogg")

        val rJson = Json.parse(JsonArraySerializer, r.text)
        for (it in rJson)
        {
            println(it.jsonObject["symbol"])
            //month+1 pentru ca month incepe de la 0
            val yesterday = "${Calendar.getInstance().get(YEAR)}-${(Calendar.getInstance().get(MONTH)+1).toString().padStart(2,'0')}-${(Calendar.getInstance().get(DAY_OF_MONTH)-1).toString().padStart(2,'0')}"
            println(yesterday)
            val symbolNews = khttp.get("https://finnhub.io/api/v1/company-news?symbol=" + it.jsonObject["symbol"].toString().removeSurrounding("\"") + "&from=" +  yesterday + "&to=" + yesterday + "&token=brmrfu7rh5rcss140ogg")

            val symbolNewsJSON = Json.parse(JsonArraySerializer, symbolNews.text)

            for (piece_of_news in symbolNewsJSON) {
                val dataToSend = piece_of_news.toString()
                println("DATA TO SEND: $dataToSend")

                sendToPython(client, dataToSend)
                println("______________________")
                sleep(20) // de schimbat la 3000 (3 secunde)
            }
        }
    }
}

fun main(args: Array<String>) {
    val getTCP = GetterTCP()
    getTCP.sendGet()
}