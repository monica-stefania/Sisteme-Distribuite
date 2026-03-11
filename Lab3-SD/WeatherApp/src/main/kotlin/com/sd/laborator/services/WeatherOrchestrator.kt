package com.sd.laborator.services

import com.sd.laborator.interfaces.AlertInterface
import com.sd.laborator.interfaces.BlackListInterface
import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.CoordinatesData
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service

@Service
class WeatherOrchestrator (
    private val locationSearchService: LocationSearchInterface,
    private val weatherForecastService: WeatherForecastInterface,
    private val blackListService: BlackListInterface,
    private val alertService: AlertInterface
    )
{
    fun getForecastData(location: String): String {

        //verific daca locatia mea imi permite sa caut informatii despre vreme
        //daca se afla afla pe black list atunci nu pot cauta informatii
        val verifyLocation: Boolean = blackListService.isLocationAllowed(location)
        if(verifyLocation == false)
            return "Nu sunt date disponibile pentru aceasta locatie {$location}!"

        //daca e totul in regula, extrag coordonatele de la locatia pe care doresc sa o caut
        val coords : CoordinatesData = locationSearchService.getLocationLatLon(location)

        //daca informatiile nu au fost gasite afisez mesajul
        if (coords.latitude == 0.0 || coords.longitude == 0.0) {
            return "Nu s-au putut gasi date meteo pentru cuvintele cheie \"$location\"!"
        }

        //daca s-au extras corect coordonatele creez un obiect WeatherForecastData
        val rawForecastData: WeatherForecastData = weatherForecastService.getForecastData(coords)

        //verific daca temperatura nu este intr-un anumit interval
        val alertMessage : String = alertService.getAlertTemperature(rawForecastData)
        if(alertMessage != "")
            return alertMessage + rawForecastData.toString()
        return rawForecastData.toString()
    }

}