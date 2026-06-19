package com.sd.laborator.business.interfaces.crudInterfaces

interface IBeerGetterByName {
    fun getBeerByName(name: String): String?
}