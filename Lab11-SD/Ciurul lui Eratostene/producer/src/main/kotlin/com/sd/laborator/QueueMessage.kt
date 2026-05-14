package com.sd.laborator

import io.micronaut.core.annotation.Introspected

@Introspected
class QueueMessage (
    val maxNumber: Int,
    val numbersList: List<Int>
)