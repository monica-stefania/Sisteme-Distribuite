package com.sd.laborator.business.services.crudServices

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.crudInterfaces.IBeerDeleter
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
open class BeerDeleterService:IBeerDeleter, AbstractBeerCrudService() {
    override fun deleteBeer(name: String) {
        if(_pattern.matcher(name).find()) {
            println("SQL Injection for beer name")
            return
        }
        _beerRepository.delete(name)
    }
}