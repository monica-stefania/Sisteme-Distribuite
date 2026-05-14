package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class EratosteneResponse {
    var message: String? = null
    var primes: List<Int>? = null
}