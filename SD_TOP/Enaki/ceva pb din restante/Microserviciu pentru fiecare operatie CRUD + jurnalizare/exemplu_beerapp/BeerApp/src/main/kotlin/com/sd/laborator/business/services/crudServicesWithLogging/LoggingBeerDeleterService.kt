package com.sd.laborator.business.services.crudServicesWithLogging

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.ILogger
import com.sd.laborator.business.interfaces.crudInterfaces.IBeerDeleter
import com.sd.laborator.business.services.crudServices.BeerDeleterService
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
class LoggingBeerDeleterService(private val logger: ILogger): BeerDeleterService() {
    override fun deleteBeer(name: String) {
        super.deleteBeer(name)
        logger.log("deleteBeer")
    }
}