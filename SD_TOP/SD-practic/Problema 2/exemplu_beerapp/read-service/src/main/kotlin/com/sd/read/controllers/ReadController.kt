package com.sd.read.controllers

import com.sd.read.models.Beer
import com.sd.read.services.IReadBeerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/read")
open class ReadController(
    private val service: IReadBeerService
) {
    @GetMapping("/beers")
    fun getAllBeers(): List<Beer> {
        return service.getAllBeers()
    }

    @GetMapping("/beer/{name}")
    fun getBeerByName(@PathVariable name: String): Beer? {
        return service.getBeerByName(name)
    }
}