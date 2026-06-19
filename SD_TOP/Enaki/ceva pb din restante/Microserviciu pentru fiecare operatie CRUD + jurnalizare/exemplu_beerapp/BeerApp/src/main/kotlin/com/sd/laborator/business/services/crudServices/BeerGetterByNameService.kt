package com.sd.laborator.business.services.crudServices

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.crudInterfaces.IBeerGetterByName
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
open class BeerGetterByNameService:IBeerGetterByName, AbstractBeerCrudService() {
    override fun getBeerByName(name: String): String? {
        if(_pattern.matcher(name).find()) {
            println("SQL Injection for beer name")
            return null
        }
        val result = _beerRepository.getByName(name)
        return result.toString()
    }
}