package com.sd.create.services

import com.sd.create.models.Beer

interface ICreateBeerService {
    fun addBeer(beer: Beer): String
}