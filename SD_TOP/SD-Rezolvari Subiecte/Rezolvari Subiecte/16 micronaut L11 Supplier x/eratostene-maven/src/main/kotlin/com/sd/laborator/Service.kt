package com.sd.laborator

import java.util.*
import javax.inject.Singleton

@Singleton
class Service {
    private val random_vect1 = Vector<Int>(100)
    private val random_vect2 = Vector<Int>(100)

    fun gasirePerechiConditie(): Vector<Pair<Int, Int>> {

        val adt = Vector<Pair<Int, Int>>(100)

        for (i in 0 until 99) {
            random_vect1.add((Math.random()*100).toInt())
            random_vect2.add((Math.random()*100).toInt())
        }

        for (a in random_vect1)
            for (b in random_vect2)
                if (a*b == a+b*3)
                    adt.addElement(Pair(a,b))

        if(adt.size == 0)
            adt.addElement(Pair(-999999, -999999))

        return adt
    }
}