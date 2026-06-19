package com.sd.update.repositories

import com.sd.update.models.Beer

interface IUpdateBeerRepository {
    fun update(beer: Beer)
}