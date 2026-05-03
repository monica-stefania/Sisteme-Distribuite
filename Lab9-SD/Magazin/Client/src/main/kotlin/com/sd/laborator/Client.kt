package com.sd.laborator

import com.sd.laborator.database.CatalogProduseRepository
import com.sd.laborator.database.Client
import com.sd.laborator.database.ClientRepository
import com.sd.laborator.database.Produs
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cglib.core.KeyFactory
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.integration.annotation.InboundChannelAdapter
import org.springframework.integration.annotation.Poller
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@EnableBinding(Source::class)
@SpringBootApplication
@RestController
@CrossOrigin(origins = ["http://localhost:5001"])
open class ClientMicroservice(
    private val clientRepository: ClientRepository,
    private val catalogProduseRepository: CatalogProduseRepository,
    private val source: Source
) {

    @Bean
    open fun initData(repo: CatalogProduseRepository): CommandLineRunner{
        return CommandLineRunner {
            if (repo.count() == 0L) {
                repo.save(Produs(denumire = "Masca protectie", pret = 5.0))
                repo.save(Produs(denumire = "Vaccin anti-COVID-19", pret = 100.0))
                repo.save(Produs(denumire = "Combinezon", pret = 50.0))
                println("✅ Produse adăugate în catalogul SQLite!")
            }
        }
    }

    @PostMapping("/login")
    fun loginClient(@RequestParam email: String, @RequestParam parola: String): ResponseEntity<Any> {
        val client = clientRepository.findByEmail(email)

        return if (client != null && client.parola == parola) {
            println("Autentificare reusita pentru: $email")
            ResponseEntity.ok(client)
        } else {
            println("Autentificare esuata pentru: $email")
            ResponseEntity.status(401).body("Email sau parola incorecta")
        }
    }

    @PostMapping("/register")
    fun registerClient(
        @RequestParam nume: String,
        @RequestParam prenume: String,
        @RequestParam email: String,
        @RequestParam parola: String,
        @RequestParam adresaLivrare: String,
        @RequestParam adresaFacturare: String,
        @RequestParam telefon: String
    ): ResponseEntity<String> {

        if (clientRepository.findByEmail(email) != null) {
            return ResponseEntity.status(400).body("Acest email este deja folosit!")
        }

        val clientNou = Client(
            nume = nume,
            prenume = prenume,
            email = email,
            parola = parola,
            adresaLivrare = adresaLivrare,
            adresaFacturare = adresaFacturare,
            telefon = telefon
        )

        clientRepository.save(clientNou)
        println("Client nou înregistrat cu succes: $email")

        return ResponseEntity.ok("Cont creat cu succes!")
    }

    @GetMapping("/produse")
    fun getProduse(): ResponseEntity<List<String>> {
        val produse = catalogProduseRepository.findAll().map { it.denumire }
        return ResponseEntity.ok(produse)
    }

    @PostMapping("/comanda")
    fun plaseazaComanda(
        @RequestParam email: String,
        @RequestParam produs: String,
        @RequestParam cantitate: Int,
        @RequestParam adresaLivrare: String
    ): ResponseEntity<String> {

        val idClient = clientRepository.findByEmail(email)!!.id
        val mesaj = "$idClient|$produs|$cantitate|$adresaLivrare"

        val sent = source.output().send(MessageBuilder.withPayload(mesaj).build())

        return if (sent) {
            println("Comandă trimisa: $mesaj")
            ResponseEntity.ok("Comanda a fost trimisa cu succes!")
        } else {
            ResponseEntity.status(500).body("Eroare la trimiterea comenzii.")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<ClientMicroservice>(*args)
}