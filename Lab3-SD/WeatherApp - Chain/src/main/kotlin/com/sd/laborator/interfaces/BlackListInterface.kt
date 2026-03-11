package com.sd.laborator.interfaces

import com.sd.laborator.pojo.CoordinatesData

interface BlackListInterface {
    fun isLocationAllowed(location: String): Boolean
}