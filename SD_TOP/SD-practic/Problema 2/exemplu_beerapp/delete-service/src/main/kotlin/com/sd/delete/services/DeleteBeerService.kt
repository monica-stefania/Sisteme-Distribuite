package com.sd.delete.services

import com.sd.delete.repositories.IDeleteBeerRepository
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
open class DeleteBeerService(
    private val repository: IDeleteBeerRepository
) : IDeleteBeerService {

    private val restTemplate = RestTemplate()

    override fun deleteBeer(name: String): String {
        repository.delete(name)

        restTemplate.postForObject(
            "http://localhost:8085/log",
            mapOf(
                "operation" to "DELETE",
                "details" to "Deleted beer: $name"
            ),
            String::class.java
        )

        return "Beer deleted successfully"
    }
}