package com.sd.laborator

import jakarta.inject.Singleton
import java.util.Vector
import javax.validation.constraints.Max

@Singleton
class EratosteneSieveService {
    val MAX_SIZE = 1000001

    //vector boolean daca e prim sau nu
    private val isPrime = Vector<Boolean>(MAX_SIZE)
    //cel mai mic factor comun dintre 2 numere
    private val SPF = Vector<Int>(MAX_SIZE)

    init{
        for (i in 0 until MAX_SIZE)
        {
            isPrime.add(true)
            SPF.add(2)
        }
        isPrime[0] = false
        isPrime[1] = false
    }

    fun findPrimesLessThan(n: Int): List<Int>
    {
        val prime: MutableList<Int> = ArrayList()
        for (i in 2 until n){
            if(isPrime[i])
            {
                prime.add(i)
                SPF[i] = i
            }

            var j = 0
            while (j < prime.size && i * prime[j] < n && prime[j] <= SPF[i])
            {
                isPrime[i * prime[j]] = false
                SPF[i * prime[j]] = prime[j]
                j++
            }
        }
        return prime
    }

    fun findNextNumberRecursive(n: Int): Int
    {
        if (n == 0)
            return 1
        else
            return findNextNumberRecursive(n - 1) + 2 * (findNextNumberRecursive(n - 1) / n)
    }

    fun listOfNumbersRecurive(n: Int): List<Int>
    {
        var numbers: MutableList<Int> = mutableListOf()
        for (i in 0 until n)
        {
            numbers.add(findNextNumberRecursive(i))
        }
        return numbers
    }
}