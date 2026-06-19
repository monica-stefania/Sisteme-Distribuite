package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class IntersectionRequest {
    var size: Int = 100
    var minValue: Int = 1
    var maxValue: Int = 200
}