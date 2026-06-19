package com.sd.update.controllers

import com.sd.update.models.Beer
import com.sd.update.services.IUpdateBeerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/update")
open class UpdateController(
    private val service: IUpdateBeerService
) {
    @PutMapping("/beer")
    fun updateBeer(@RequestBody beer: Beer): String {
        return service.updateBeer(beer)
    }
}