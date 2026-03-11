package com.sd.laborator.services

import com.sd.laborator.interfaces.AlertInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service

@Service
class AlertService : AlertInterface {
    private val min_temp = 5.0
    private val max_temp = 15.0

    override fun getAlertTemperature(weatherForecastData: WeatherForecastData): String {
        if (weatherForecastData.minTemp < min_temp)
            return "Temperatura locatiei cu coordonatele ${weatherForecastData.latitude}, ${weatherForecastData.longitude}\n" +
                    "este de ${weatherForecastData.minTemp} grade este sub temperatura minima $min_temp grade\n"
        else if (weatherForecastData.maxTemp > max_temp)
            return "Temperatura locatiei cu coordonatele ${weatherForecastData.latitude}, ${weatherForecastData.longitude}\n " +
                    "este de ${weatherForecastData.maxTemp} grade este peste temperatura maxima $max_temp grade\n"
        return ""
    }
}