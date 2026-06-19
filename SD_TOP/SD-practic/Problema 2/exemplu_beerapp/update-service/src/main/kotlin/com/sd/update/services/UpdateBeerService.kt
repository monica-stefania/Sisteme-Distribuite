package com.sd.update.services

import com.sd.update.models.Beer
import com.sd.update.repositories.IUpdateBeerRepository
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
open class UpdateBeerService(
    private val repository: IUpdateBeerRepository
) : IUpdateBeerService {

    private val restTemplate = RestTemplate()

    override fun updateBeer(beer: Beer): String {
        repository.update(beer)

        restTemplate.postForObject(
            "http://localhost:8085/log",
            mapOf(
                "operation" to "UPDATE",
                "details" to "Updated beer id: ${beer.beerID}"
            ),
            String::class.java
        )

        return "Beer updated successfully"
    }
}