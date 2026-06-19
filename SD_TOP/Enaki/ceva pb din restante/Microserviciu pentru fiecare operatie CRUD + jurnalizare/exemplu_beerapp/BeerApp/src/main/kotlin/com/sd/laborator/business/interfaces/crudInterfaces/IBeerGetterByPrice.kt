package com.sd.laborator.business.interfaces.crudInterfaces

interface IBeerGetterByPrice {
    fun getBeerByPrice(price: Float): String
}