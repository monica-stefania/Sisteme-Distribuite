package com.sd.laborator
import io.micronaut.core.annotation.Introspected

@Introspected
class EratosteneRequest {
    var maxNumber: Int = 0
    var numbersToCheck: List<Int>? = null

}