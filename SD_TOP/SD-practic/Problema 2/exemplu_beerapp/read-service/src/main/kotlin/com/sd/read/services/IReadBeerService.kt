package com.sd.read.services

import com.sd.read.models.Beer

interface IReadBeerService {
    fun getAllBeers(): List<Beer>
    fun getBeerByName(name: String): Beer?
}