package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class EratosteneRequest {
    private var number: Int = 0

    fun getNumber(): Int {
        return number.toInt()
    }
    fun setNumber(n: Int)
    {
        number = n
    }
}