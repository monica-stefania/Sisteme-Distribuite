package com.sd.laborator.business.services.crudServices

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.crudInterfaces.IBeersGetter
import com.sd.laborator.models.Beer
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
open class BeersGetterService:IBeersGetter, AbstractBeerCrudService() {
    override fun getBeers(): String {
        val result: MutableList<Beer?> = _beerRepository.getAll()
        var stringResult: String = ""
        for (item in result) {
            stringResult += item
        }
        return stringResult
    }
}