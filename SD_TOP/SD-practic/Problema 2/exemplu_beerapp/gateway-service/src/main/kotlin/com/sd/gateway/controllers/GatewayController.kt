package com.sd.gateway.controllers

import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/api")
class GatewayController {

    private val restTemplate = RestTemplate()

    @PostMapping("/beer")
    fun createBeer(@RequestBody beer: Any): String {
        return restTemplate.postForObject(
            "http://localhost:8081/create/beer",
            beer,
            String::class.java
        ) ?: "Error"
    }

    @GetMapping("/beers")
    fun getAllBeers(): String {
        return restTemplate.getForObject(
            "http://localhost:8082/read/beers",
            String::class.java
        ) ?: "Error"
    }

    @GetMapping("/beer/{name}")
    fun getBeerByName(@PathVariable name: String): String {
        return restTemplate.getForObject(
            "http://localhost:8082/read/beer/$name",
            String::class.java
        ) ?: "Error"
    }

    @PutMapping("/beer")
    fun updateBeer(@RequestBody beer: Any): String {
        restTemplate.put(
            "http://localhost:8083/update/beer",
            beer
        )
        return "Request sent to update-service"
    }

    @DeleteMapping("/beer/{name}")
    fun deleteBeer(@PathVariable name: String): String {
        restTemplate.delete(
            "http://localhost:8084/delete/beer/$name"
        )
        return "Request sent to delete-service"
    }
}