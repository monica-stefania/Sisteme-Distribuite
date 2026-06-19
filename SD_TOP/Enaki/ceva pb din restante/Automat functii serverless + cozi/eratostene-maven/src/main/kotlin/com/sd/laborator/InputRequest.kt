package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class InputRequest {
    private lateinit var input: Integer

    fun getInput(): Int {
        return input.toInt()
    }
}