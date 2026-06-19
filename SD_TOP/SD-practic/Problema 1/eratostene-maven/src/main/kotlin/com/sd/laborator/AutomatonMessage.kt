package com.sd.laboratory

data class AutomatonMessage(
    val number: Int,
    var state: String = "START",
    val primes: MutableList<Int> = mutableListOf()
)