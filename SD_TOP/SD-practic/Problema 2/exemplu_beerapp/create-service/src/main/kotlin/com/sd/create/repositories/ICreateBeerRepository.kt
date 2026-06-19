package com.sd.create.repositories

import com.sd.create.models.Beer

interface ICreateBeerRepository {
    fun createTable()
    fun add(beer: Beer)
}