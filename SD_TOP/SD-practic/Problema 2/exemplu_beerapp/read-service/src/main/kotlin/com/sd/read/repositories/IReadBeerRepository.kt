package com.sd.read.repositories

import com.sd.read.models.Beer

interface IReadBeerRepository {
    fun getAll(): List<Beer>
    fun getByName(name: String): Beer?
}