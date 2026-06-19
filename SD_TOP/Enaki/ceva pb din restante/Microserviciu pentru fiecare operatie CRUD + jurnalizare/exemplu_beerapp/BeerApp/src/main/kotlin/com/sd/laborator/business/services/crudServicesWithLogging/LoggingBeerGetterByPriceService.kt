package com.sd.laborator.business.services.crudServicesWithLogging

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.ILogger
import com.sd.laborator.business.interfaces.crudInterfaces.IBeerGetterByPrice
import com.sd.laborator.business.services.crudServices.BeerGetterByPriceService
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
class LoggingBeerGetterByPriceService(private val logger: ILogger): BeerGetterByPriceService() {
    override fun getBeerByPrice(price: Float): String {
        logger.log("getBeerByPrice")
        return super.getBeerByPrice(price)
    }
}