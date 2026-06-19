package com.sd.laborator

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.runtime.Micronaut

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.run(Application::class.java, *args)
    }

    @Controller
    class LambdaController {
        private var currentState = 0

        init {
            println("Stare initiala: $currentState")
        }

        @Post
        fun execute(@Body request: InputRequest): OutputResponse {
            val serverlessFunctionOutputResponse = handlers[currentState].apply(request)
            currentState = serverlessFunctionOutputResponse.getNewState()!!
            val outputResponse = OutputResponse()
            outputResponse.setMessage(serverlessFunctionOutputResponse.getMessage())
            outputResponse.setOutput(serverlessFunctionOutputResponse.getOutput())
            return outputResponse
        }

        companion object {
            private val handlers = listOf(State0(), State1(), State2())
            // private val handler = AbstractState()
        }
    }
}