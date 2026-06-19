package com.sd.laborator

import java.util.*
import javax.inject.Singleton

@Singleton
class Service {
    private val random_vect1 = Vector<Int>(100)
    private val random_vect2 = Vector<Int>(100)

    fun getIntersection(): Set<Int> {

        for (i in 0 until 99) {
            random_vect1.add((Math.random()*100).toInt())
            random_vect2.add((Math.random()*100).toInt())
        }

        val adt = random_vect1.intersect(random_vect2)

        return adt
    }
}