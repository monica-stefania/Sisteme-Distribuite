package com.sd.delete.controllers

import com.sd.delete.services.IDeleteBeerService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/delete")
open class DeleteController(
    private val service: IDeleteBeerService
) {
    @DeleteMapping("/beer/{name}")
    fun deleteBeer(@PathVariable name: String): String {
        return service.deleteBeer(name)
    }
}