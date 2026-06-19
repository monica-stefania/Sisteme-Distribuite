package com.sd.laborator.business.services.crudServicesWithLogging

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.ILogger
import com.sd.laborator.business.interfaces.crudInterfaces.IBeersGetter
import com.sd.laborator.business.services.crudServices.BeersGetterService
import com.sd.laborator.models.Beer
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
class LoggingBeersGetterService(private val logger: ILogger): BeersGetterService() {
    override fun getBeers(): String {
        logger.log("getBeers")
        return super.getBeers()
    }
}