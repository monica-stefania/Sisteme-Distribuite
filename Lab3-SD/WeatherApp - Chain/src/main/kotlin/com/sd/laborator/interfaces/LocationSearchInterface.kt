package com.sd.laborator.interfaces

import com.sd.laborator.pojo.CoordinatesData

interface LocationSearchInterface {
    fun getLocationLatLon(locationName: String): CoordinatesData
}