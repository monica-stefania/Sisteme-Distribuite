package com.sd.laborator.business.services.crudServices

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.crudInterfaces.IBeerUpdater
import com.sd.laborator.models.Beer
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
open class BeerUpdaterService:IBeerUpdater, AbstractBeerCrudService() {
    override fun updateBeer(beer: Beer) {
        if(_pattern.matcher(beer.beerName).find()) {
            println("SQL Injection for beer name")
            return
        }
        _beerRepository.update(beer)
    }
}