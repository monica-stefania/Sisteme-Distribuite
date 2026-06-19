package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function
import javax.inject.Inject

// echo '{"number": "8"}' | java -jar target/eratostene-0.1.jar

@FunctionBean("eratostene")
class EratosteneFunction : FunctionInitializer(), Function<EratosteneRequest, EratosteneResponse> {
    @Inject
    private lateinit var service: Service

    private val LOG: Logger = LoggerFactory.getLogger(EratosteneFunction::class.java)

    override fun apply(request: EratosteneRequest) : EratosteneResponse {
        // preluare numar din parametrul de intrare al functiei
        val seed = request.getNumber()

        val response = EratosteneResponse()

        // se face calculul si se seteaza proprietatile pe obiectul cu rezultatul
        response.setResponse(service.calcul(seed.toLong()))
        response.setMessage("Calcul efectuat cu succes!")

        LOG.info("Calcul incheiat!")

        return response
    }   
}

fun main(args : Array<String>) { 
    val function = EratosteneFunction()
    function.run(args, { context -> function.apply(context.get(EratosteneRequest::class.java))})
}