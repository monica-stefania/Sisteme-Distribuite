package com.sd.laborator;

import io.micronaut.function.executor.FunctionInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.Function

abstract class AbstractState(private val stateNumber: Int,
                             private val inputToNewStateAndOutput: Map<Int, Pair<Int, Int>>)
    : FunctionInitializer(), Function<InputRequest, OutputResponse> {
    private val LOG: Logger = LoggerFactory.getLogger(AbstractState::class.java)
    private fun validateInput(msg: InputRequest): Boolean = msg.getInput() in listOf(0, 1)
    private fun getErrorResponse(): ServerlessFunctionOutputResponse {
        val response = ServerlessFunctionOutputResponse()
        response.setMessage("Input invalid! Valori permise: 0, 1.")
        response.setNewState(stateNumber)
        response.setOutput(-1)
        return response
    }

    override fun apply(msg : InputRequest) : ServerlessFunctionOutputResponse {
        if(!validateInput(msg)) {
            LOG.info("Input invalid: ${msg.getInput()}")
            return getErrorResponse()
        }
        val newStateAndOutput = inputToNewStateAndOutput[msg.getInput()]!!
        val response = ServerlessFunctionOutputResponse()
        response.setMessage("OK")
        response.setNewState(newStateAndOutput.first)
        response.setOutput(newStateAndOutput.second)
        LOG.info("Input: ${msg.getInput()} =>\n\tNoua stare: ${response.getNewState()}\n\tOutput: ${response.getOutput()}")
        return response
    }
}

class State0: AbstractState(0,
    mapOf(0 to Pair(0, 0),
        1 to Pair(1, 1)
    )
)

class State1: AbstractState(1,
    mapOf(0 to Pair(2, 1),
        1 to Pair(1, 0)
    )
)

class State2: AbstractState(2,
    mapOf(0 to Pair(2, 0),
        1 to Pair(0, 1)
    )
)

/**
 * This main method allows running the function as a CLI application using: echo '{}' | java -jar function.jar 
 * where the argument to echo is the JSON to be parsed.
 */
fun main(args : Array<String>) { 
    // val function = AbstractState()
    // function.run(args, { context -> function.apply(context.get(InputRequest::class.java))})
}