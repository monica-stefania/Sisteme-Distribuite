package com.sd.laborator.interfaces

import com.sd.laborator.pojo.WeatherForecastData

interface AlertInterface {
    fun getAlertTemperature(weatherForecastData: WeatherForecastData): String
}