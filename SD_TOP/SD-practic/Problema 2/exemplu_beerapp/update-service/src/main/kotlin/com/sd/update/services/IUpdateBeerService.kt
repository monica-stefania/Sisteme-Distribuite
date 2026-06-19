package com.sd.update.services

import com.sd.update.models.Beer

interface IUpdateBeerService {
    fun updateBeer(beer: Beer): String
}