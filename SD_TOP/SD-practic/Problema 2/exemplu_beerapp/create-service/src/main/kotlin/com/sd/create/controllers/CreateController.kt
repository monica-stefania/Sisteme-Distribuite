package com.sd.create.controllers

import com.sd.create.models.Beer
import com.sd.create.services.ICreateBeerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/create")
class CreateController(
    private val service: ICreateBeerService
) {
    @PostMapping("/beer")
    fun createBeer(@RequestBody beer: Beer): String {
        return service.addBeer(beer)
    }
}