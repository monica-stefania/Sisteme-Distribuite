package com.sd.laborator.business.services.crudServicesWithLogging

import com.sd.laborator.business.interfaces.ILogger
import com.sd.laborator.business.services.crudServices.BeerAdderService
import com.sd.laborator.models.Beer
import org.springframework.stereotype.Service

@Service
class LoggingBeerAdderService(private val logger: ILogger):BeerAdderService() {
    override fun addBeer(beer: Beer) {
        super.addBeer(beer)
        logger.log("addBeer")
    }
}