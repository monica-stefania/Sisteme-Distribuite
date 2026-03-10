package com.sd.laborator.interfaces

import com.sd.laborator.pojo.CoordinatesData
import com.sd.laborator.pojo.WeatherForecastData

interface WeatherForecastInterface {
     fun getForecastData(coords: CoordinatesData): WeatherForecastData
}