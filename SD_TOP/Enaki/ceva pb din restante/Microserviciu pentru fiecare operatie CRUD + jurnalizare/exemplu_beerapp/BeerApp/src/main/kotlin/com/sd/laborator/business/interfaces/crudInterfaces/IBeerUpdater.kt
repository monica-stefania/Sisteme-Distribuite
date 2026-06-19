package com.sd.laborator.business.interfaces.crudInterfaces

import com.sd.laborator.models.Beer

interface IBeerUpdater {
    fun updateBeer(beer: Beer)
}