package com.sd.laborator

import java.util.*
import javax.inject.Singleton

@Singleton
class Service {
    private val random_vect = Vector<Int>(100)

    fun calcul(seed: Long): List<Int> {
        val adt = Vector<Int>(100)

        for (i in 0 until 99) {
            random_vect.add((Math.random()*100).toInt())
            adt.add(random_vect[i]*random_vect[i])
        }

        return adt
    }
}