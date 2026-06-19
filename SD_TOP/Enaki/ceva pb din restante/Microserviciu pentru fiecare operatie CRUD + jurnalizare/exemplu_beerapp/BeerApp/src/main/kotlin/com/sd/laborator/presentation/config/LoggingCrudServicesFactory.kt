package com.sd.laborator.presentation.config

import com.sd.laborator.business.interfaces.ILogger
import com.sd.laborator.business.interfaces.crudInterfaces.*
import com.sd.laborator.business.services.crudServicesWithLogging.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
open class LoggingCrudServicesFactory {
    @Value("\${logging.crudLogs}")
    private lateinit var fileName: String

    @Autowired
    private lateinit var logger: ILogger

    @Autowired
    fun setFileName() {
        logger.setFileName(fileName)
    }

    @Bean
    @Primary
    open fun getAddBeerService(): IBeerAdder = LoggingBeerAdderService(logger)

    @Bean
    @Primary
    open fun getBeerDeleterService(): IBeerDeleter = LoggingBeerDeleterService(logger)

    @Bean
    @Primary
    open fun getBeerGetterByName(): IBeerGetterByName = LoggingBeerGetterByNameService(logger)

    @Bean
    @Primary
    open fun getBeerGetterByPrice(): IBeerGetterByPrice = LoggingBeerGetterByPriceService(logger)

    @Bean
    @Primary
    open fun getBeersGetter(): IBeersGetter = LoggingBeersGetterService(logger)

    @Bean
    @Primary
    open fun getBeerTableCreator(): IBeerTableCreator = LoggingBeerTableCreatorService(logger)

    @Bean
    @Primary
    open fun getBeerUpdater(): IBeerUpdater = LoggingBeerUpdaterService(logger)
}