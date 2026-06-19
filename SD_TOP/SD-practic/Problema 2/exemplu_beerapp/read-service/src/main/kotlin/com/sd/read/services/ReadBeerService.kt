package com.sd.read.services

import com.sd.read.models.Beer
import com.sd.read.repositories.IReadBeerRepository
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
open class ReadBeerService(
    private val repository: IReadBeerRepository
) : IReadBeerService {

    private val restTemplate = RestTemplate()

    override fun getAllBeers(): List<Beer> {
        restTemplate.postForObject(
            "http://localhost:8085/log",
            mapOf(
                "operation" to "READ",
                "details" to "Read all beers"
            ),
            String::class.java
        )

        return repository.getAll()
    }

    override fun getBeerByName(name: String): Beer? {
        restTemplate.postForObject(
            "http://localhost:8085/log",
            mapOf(
                "operation" to "READ",
                "details" to "Read beer by name: $name"
            ),
            String::class.java
        )

        return repository.getByName(name)
    }
}