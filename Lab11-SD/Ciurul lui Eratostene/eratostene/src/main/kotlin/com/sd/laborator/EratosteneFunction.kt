package com.sd.laborator

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import jakarta.inject.Inject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function

@FunctionBean("erotestene")
class EratosteneFunction: FunctionInitializer(), Function<EratosteneRequest, EratosteneResponse> {

    @Inject
    private lateinit var eratosteneSieveService: EratosteneSieveService

    private val LOG: Logger = LoggerFactory.getLogger(EratosteneFunction::class.java)

    override fun apply(msg: EratosteneRequest): EratosteneResponse{
        val number = msg.maxNumber

        val response = EratosteneResponse()
        if(number >= eratosteneSieveService.MAX_SIZE) {
            LOG.error("Parametru prea mare! $number > maximul de ${eratosteneSieveService.MAX_SIZE}")
            return response
        }

        LOG.info("Se calculeaza primele $number numere prime...")

        val allPrimes = eratosteneSieveService.findPrimesLessThan(number)

        LOG.info("Se extrag doar numerele prime din lista primita")
        val listOfNumbers = msg.numbersToCheck ?: emptyList()
        val primes = listOfNumbers.filter { allPrimes.contains(it) }

        response.setPrimes(primes)
        response.setMessage("Calcul efecturat cu succes!")

        /*
        response.setRecursive(eratosteneSieveService.listOfNumbersRecurive(number))
        response.setMessage("Aflarea numerelor a fost efectuata cu succes!")
        *
         */
        LOG.info("Calcul incheiat!")
        return response
    }
}

fun main(args: Array<String>){
    val function = EratosteneFunction()
    function.run(args, { context -> function.apply(context.get(EratosteneRequest::class.java))})
}