package com.sd.laborator.business.services.crudServicesWithLogging

import com.sd.laborator.business.abstractClasses.AbstractBeerCrudService
import com.sd.laborator.business.interfaces.ILogger
import com.sd.laborator.business.interfaces.crudInterfaces.IBeerGetterByName
import com.sd.laborator.business.services.crudServices.BeerGetterByNameService
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
class LoggingBeerGetterByNameService(private val logger: ILogger): BeerGetterByNameService() {
    override fun getBeerByName(name: String): String? {
        logger.log("getBeerByName")
        return super.getBeerByName(name)
    }
}