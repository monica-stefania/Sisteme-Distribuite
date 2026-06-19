package com.sd.laborator.business.services.crudServicesWithLogging

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.ILogger
import com.sd.laborator.business.interfaces.crudInterfaces.IBeerTableCreator
import com.sd.laborator.business.services.crudServices.BeerTableCreatorService
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
class LoggingBeerTableCreatorService(private val logger: ILogger): BeerTableCreatorService() {
    override fun createBeerTable() {
        super.createBeerTable()
        logger.log("createBeerTable")
    }
}