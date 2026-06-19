package com.sd.create.services

import com.sd.create.models.Beer
import com.sd.create.repositories.ICreateBeerRepository
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CreateBeerService(
    private val repository: ICreateBeerRepository
) : ICreateBeerService {

    private val restTemplate = RestTemplate()

    override fun addBeer(beer: Beer): String {
        repository.createTable()
        repository.add(beer)

        restTemplate.postForObject(
            "http://localhost:8085/log",
            mapOf(
                "operation" to "CREATE",
                "details" to "Added beer: ${beer.beerName}"
            ),
            String::class.java
        )

        return "Beer created successfully"
    }
}