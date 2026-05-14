package com.sd.laborator

class EratosteneResponse {
    private var message: String? = null
    private var primes: List<Int>? = null
    //private var recursive: List<Int>? = null

    fun getPrimes(): List<Int>?{
        return primes
    }

    fun setPrimes(primes: List<Int>?){
        this.primes = primes
    }

    fun getMessage(): String?{
        return message
    }

    fun setMessage(message: String?){
        this.message = message
    }

    /*
    fun getRecursive(): List<Int>?
    {
        return recursive
    }

    fun setRecursive(numbers: List<Int>?){
        recursive = numbers;
    }

     */
}