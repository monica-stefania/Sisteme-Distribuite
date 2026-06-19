package com.sd.laborator;

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function
import jakarta.inject.Inject

@FunctionBean("eratostene")
class EratosteneFunction : FunctionInitializer(), Function<EratosteneRequest, EratosteneResponse> {
    @Inject
    private lateinit var eratosteneSieveService: EratosteneSieveService

    private val LOG: Logger = LoggerFactory.getLogger(EratosteneFunction::class.java)

    override fun apply(msg : EratosteneRequest) : EratosteneResponse {
        // preluare numar din parametrul de intrare al functiei
        val number = msg.getNumber()

        val response = EratosteneResponse()

        if (number < 0) {
            LOG.error("N trebuie sa fie strict pozitiv")
            response.setMessage("Eroare: n trebuie să fie strict pozitiv!")
            return response
        }

        LOG.info("Se calculeaza...")

        val b = eratosteneSieveService.calculate(number)
        response.setResult(b)
        response.setMessage("Calcul efectuat cu succes pentru n = $number")
        LOG.info("Calcul efectuat cu succes pentru n = $number")

        LOG.info("Calcul incheiat!")

        return response
    }
}

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) {
    val function = EratosteneFunction()
    function.run(args, { context -> function.apply(context.get(EratosteneRequest::class.java))})
}