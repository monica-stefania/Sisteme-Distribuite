package com.sd.laborator.business.services.crudServices

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.crudInterfaces.IBeerTableCreator
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
open class BeerTableCreatorService:IBeerTableCreator, AbstractBeerCrudService() {
    override fun createBeerTable() {
        _beerRepository.createTable()
    }
}