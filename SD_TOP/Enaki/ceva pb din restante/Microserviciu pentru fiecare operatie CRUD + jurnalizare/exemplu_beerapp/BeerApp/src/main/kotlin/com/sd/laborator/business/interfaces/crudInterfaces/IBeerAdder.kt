package com.sd.laborator.business.interfaces.crudInterfaces

import com.sd.laborator.models.Beer

interface IBeerAdder {
    fun addBeer(beer: Beer)
}