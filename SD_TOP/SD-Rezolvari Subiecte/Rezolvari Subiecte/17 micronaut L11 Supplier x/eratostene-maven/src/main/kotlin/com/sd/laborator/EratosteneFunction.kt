package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Supplier
import javax.inject.Inject

// java -jar target/eratostene-0.1.jar

@FunctionBean("eratostene")
class EratosteneFunction : FunctionInitializer(), Supplier<EratosteneResponse> {
    @Inject
    private lateinit var service: Service

    private val LOG: Logger = LoggerFactory.getLogger(EratosteneFunction::class.java)

    override fun get() : EratosteneResponse {
        val response = EratosteneResponse()

        // se face calculul si se seteaza proprietatile pe obiectul cu rezultatul
        response.setResponse(service.getIntersection())
        response.setMessage("Calcul efectuat cu succes!")

        LOG.info("Calcul incheiat!")

        return response
    }   
}

fun main(args : Array<String>) { 
    val function = EratosteneFunction()
    function.run(args) { context -> function.get()}
}