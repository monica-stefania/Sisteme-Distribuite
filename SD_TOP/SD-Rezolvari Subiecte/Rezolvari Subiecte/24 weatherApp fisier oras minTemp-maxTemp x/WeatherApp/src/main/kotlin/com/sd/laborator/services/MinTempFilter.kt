package com.sd.laborator.services

import com.sd.laborator.interfaces.FilterInterface
import com.sd.laborator.pojo.WeatherForecastData

class MinTempFilter: FilterInterface {
    override fun execute(data: WeatherForecastData): Boolean {
        if(data.minTemp>10)
            return true
        return false
    }

}