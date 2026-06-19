package com.sd.laborator.business.services.crudServicesWithLogging

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.ILogger
import com.sd.laborator.business.interfaces.crudInterfaces.IBeerUpdater
import com.sd.laborator.business.services.crudServices.BeerUpdaterService
import com.sd.laborator.models.Beer
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
class LoggingBeerUpdaterService(private val logger: ILogger): BeerUpdaterService() {
    override fun updateBeer(beer: Beer) {
        super.updateBeer(beer)
        logger.log("updateBeer")
    }
}