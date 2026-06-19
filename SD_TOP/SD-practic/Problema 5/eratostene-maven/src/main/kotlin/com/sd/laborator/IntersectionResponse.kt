package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class IntersectionResponse {
    var message: String? = null
    var a: List<Int>? = null
    var b: List<Int>? = null
    var c: List<Int>? = null
}