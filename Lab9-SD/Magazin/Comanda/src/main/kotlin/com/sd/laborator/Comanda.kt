package com.sd.laborator

import com.sd.laborator.database.Comanda
import com.sd.laborator.database.ComandaRepository
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import org.springframework.messaging.support.MessageBuilder
import java.text.DateFormat
import java.text.SimpleDateFormat
import kotlin.random.Random

@EnableBinding(Processor::class)
@SpringBootApplication
class ComandaMicroservice(
    private val comandaRepository: ComandaRepository
) {
    private fun pregatireComanda(client: Long, produs: String, cantitate: Long): Long {
        println("Se pregateste comanda $cantitate x \"$produs\"...")
        val comandaNoua = Comanda(id_client = client, produs = produs, cantitate = cantitate)
        val comandaSalvata = comandaRepository.save(comandaNoua)
        println("Comanda salvata in baza de date: ${comandaSalvata.id}")
        return comandaSalvata.id
    }

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    fun preluareComanda(comanda: String?): String {
        val (identitateClient, produsComandat, cantitate, adresaLivrare) = comanda!!.split("|")
        println("Am primit comanda urmatoare: ")
        println("$identitateClient | $produsComandat | $cantitate | $adresaLivrare")

        val idComanda = pregatireComanda(identitateClient.toLong(), produsComandat, cantitate.toLong())
        val comanda = "$idComanda|$produsComandat|$cantitate"
        return comanda
    }
}

fun main(args: Array<String>) {
    runApplication<ComandaMicroservice>(*args)
}