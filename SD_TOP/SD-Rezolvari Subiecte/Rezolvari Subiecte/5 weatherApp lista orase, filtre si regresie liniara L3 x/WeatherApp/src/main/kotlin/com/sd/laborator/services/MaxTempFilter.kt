package com.sd.laborator.services

import com.sd.laborator.interfaces.FilterInterface
import com.sd.laborator.pojo.WeatherForecastData

class MaxTempFilter: FilterInterface {
    override fun execute(data: WeatherForecastData): Boolean {
        if(data.maxTemp<25)
            return true
        return false
    }
}