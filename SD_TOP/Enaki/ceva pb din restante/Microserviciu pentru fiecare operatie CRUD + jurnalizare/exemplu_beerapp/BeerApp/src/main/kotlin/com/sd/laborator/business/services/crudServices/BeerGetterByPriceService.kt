package com.sd.laborator.business.services.crudServices

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.crudInterfaces.IBeerGetterByPrice
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
open class BeerGetterByPriceService: IBeerGetterByPrice, AbstractBeerCrudService() {
    override fun getBeerByPrice(price: Float): String {
        val result = _beerRepository.getByPrice(price)
        var stringResult: String = ""
        for (item in result) {
            stringResult += item
        }
        return stringResult
    }
}