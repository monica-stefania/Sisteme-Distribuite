package com.sd.laborator

import java.util.*
import jakarta.inject.Singleton

@Singleton
class EratosteneSieveService {
    fun calculate(n: Int): Double {
        if (n < 1) return 0.0

        var a = 1.0   // a_0 = 1
        var suma = 0.0

        for (i in 1..n) {
            val aPrev = a
            a = aPrev + 2.0 * (aPrev / i)   // a_i = a_{i-1} + 2 * (a_{i-1}/i)
            suma += a * a                    // b_n = sum(a_i^2), i=1..n
        }
        return suma
    }
}