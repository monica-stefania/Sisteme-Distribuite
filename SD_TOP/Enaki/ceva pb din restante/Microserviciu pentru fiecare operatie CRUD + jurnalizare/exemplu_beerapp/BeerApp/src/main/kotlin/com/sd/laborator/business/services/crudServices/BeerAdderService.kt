package com.sd.laborator.business.services.crudServices

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.crudInterfaces.IBeerAdder
import com.sd.laborator.models.Beer
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
open class BeerAdderService:IBeerAdder, AbstractBeerCrudService() {
    override fun addBeer(beer: Beer) {
        if(_pattern.matcher(beer.beerName).find()) {
            println("SQL Injection for beer name")
            return
        }
        _beerRepository.add(beer)
    }
}