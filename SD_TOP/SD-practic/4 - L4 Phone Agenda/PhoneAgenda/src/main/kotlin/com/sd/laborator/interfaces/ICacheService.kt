package com.sd.laborator.interfaces

import com.sd.laborator.pojo.Person

interface ICacheService {
    fun get(key: String): List<Person>?
    fun put(key: String, value: List<Person>)
    fun delete(key: String)
    fun deleteAll()
}