package com.sd.laborator

import com.sd.laborator.database.Stoc
import com.sd.laborator.database.StocRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.context.annotation.Bean
import org.springframework.integration.annotation.Transformer
import org.springframework.messaging.support.MessageBuilder
import kotlin.random.Random

@EnableBinding(Processor::class)
@SpringBootApplication
class DepozitMicroservice(
    private val stocRepository: StocRepository
) {
    @Bean
    fun initStoc(): CommandLineRunner {
        return CommandLineRunner {
            try {
                if (stocRepository.count() == 0L) {
                    stocRepository.save(Stoc(produs = "Masca protectie", stoc_disponibil = 100))
                    stocRepository.save(Stoc(produs = "Vaccin anti-COVID-19", stoc_disponibil = 20))
                    stocRepository.save(Stoc(produs = "Combinezon", stoc_disponibil = 30))
                    stocRepository.save(Stoc(produs = "Manusa chirurgicala", stoc_disponibil = 40))
                    println("Stocul initial a fost adaugat in baza de date a depozitului")
                }
            } catch (e: Exception) {
                println("Eroare la adaugarea produselor in baza de date!")
            }
        }
    }

    private fun acceptareComanda(identificator: Int, produs: String, cantitate: Int): String {
        println("Comanda cu identificatorul $identificator a fost acceptata!")
        val stoc = stocRepository.findByProdus(produs)
        if(stoc != null) {
            val stocActualizat = stoc.copy(stoc_disponibil = stoc.stoc_disponibil - cantitate)
            stocRepository.save(stocActualizat)
            println("Produsul $produs a fost scazut din stoc cu $cantitate")
        }
        return "$identificator:APROBATA"
    }

    private fun respingereComanda(identificator: Int): String {
        println("Comanda cu identificatorul $identificator a fost respinsa! Stoc insuficient.")
        return "$identificator:RESPINSA"
    }

    private fun verificareStoc(produs: String, cantitate: Int): Boolean {
        val stoc = stocRepository.findByProdus(produs)
        if(stoc != null && stoc.stoc_disponibil >= cantitate)
            return true
        return false
    }

    private fun pregatireColet(produs: String, cantitate: Int): String {
        ///TODO - retragere produs de pe stoc in cantitatea specificata
        println("Produsul $produs in cantitate de $cantitate buc. este pregatit de livrare.")
        return "$produs|$cantitate"
    }

    @Transformer(inputChannel = Processor.INPUT, outputChannel = Processor.OUTPUT)
    fun procesareComanda(comanda: String?): String {
        val (identificatorComanda, produs, cantitate) = comanda!!.split("|")
        println("Procesez comanda cu identificatorul $identificatorComanda...")

        val rezultatProcesareComanda: String = if (verificareStoc(produs, cantitate.toInt())) {
            acceptareComanda(identificatorComanda.toInt(), produs, cantitate.toInt())
        } else {
            respingereComanda(identificatorComanda.toInt())
        }

        return rezultatProcesareComanda
    }
}

fun main(args: Array<String>) {
    runApplication<DepozitMicroservice>(*args)
}