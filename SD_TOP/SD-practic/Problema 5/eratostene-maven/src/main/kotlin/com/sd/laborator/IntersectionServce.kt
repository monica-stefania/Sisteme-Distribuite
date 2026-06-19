package com.sd.laborator

import jakarta.inject.Singleton
import kotlin.random.Random

@Singleton
class IntersectionService {

    fun generateADT(size: Int, minValue: Int, maxValue: Int): MutableList<Int> {
        val result = mutableListOf<Int>()

        repeat(size) {
            result.add(Random.nextInt(minValue, maxValue + 1))
        }

        return result
    }

    fun intersection(a: List<Int>, b: List<Int>): List<Int> {
        return a.intersect(b.toSet()).sorted()
    }
}