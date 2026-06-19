package com.sd.laborator

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.Micronaut
import kotlin.random.Random

// ADT A - 100 valori initializate aleator (n de la 1 la 100)
// ADT B - rezultatele b_n corespunzatoare
data class ADTEntry(val n: Int, val bN: Double)

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application::class.java, *args)
    }

    @Controller
    class LambdaController {
        @Post
        fun execute(@Body request: EratosteneRequest): EratosteneResponse {
            return handler.apply(request)
        }

        // Endpoint pentru a vedea toate rezultatele din ADT B
        @Get("/results")
        fun getResults(): List<ADTEntry> {
            return adtB
        }

        // Endpoint pentru a vedea valorile din ADT A
        @Get("/inputs")
        fun getInputs(): List<Int> {
            return adtA
        }

        companion object {
            private val handler = EratosteneFunction()

            // ADT A: 100 valori n aleatoare in intervalul [1, 100]
            val adtA: List<Int> = List(100) { Random.nextInt(1, 101) }

            // ADT B: rezultatele b_n pentru fiecare n din A
            val adtB: List<ADTEntry> = adtA.map { n ->
                val req = EratosteneRequest().apply { setNumber(n) }
                val resp = handler.apply(req)
                ADTEntry(n, resp.getResult())
            }
        }
    }
}