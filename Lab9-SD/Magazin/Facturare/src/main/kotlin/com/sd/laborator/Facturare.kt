package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.integration.annotation.Transformer
import java.util.Date
import kotlin.math.abs
import kotlin.random.Random
import kotlin.random.nextUInt

@EnableBinding(Processor::class)
@SpringBootApplication
class FacturareMicroservice(
    private val facturaRepository: FacturaRepository
) {
    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    fun emitereFactura(comanda: String?): String {
        val (identitateComanda, mesaj) = comanda!!.split(":")
        if(mesaj == "APROBAT")
            println("Emit factura pentru comanda $comanda...")
        else if(mesaj == "RESPINS")
            println("Comanda a fost respinsa $comanda")

        val factura_salvata = facturaRepository.save(Factura(id_comanda = identitateComanda.toLong(), data = Date()))

        println("S-a emis factura cu nr ${factura_salvata.nr_inregistrare}.")

        return "$identitateComanda"
    }
}

fun main(args: Array<String>) {
    runApplication<FacturareMicroservice>(*args)
}