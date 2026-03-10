package com.sd.laborator.controllers

import com.sd.laborator.interfaces.AlertInterface
import com.sd.laborator.interfaces.BlackListInterface
import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.CoordinatesData
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class WeatherAppOrchestrator {
    @Autowired
    private lateinit var locationSearchService: LocationSearchInterface

    @Autowired
    private lateinit var weatherForecastService: WeatherForecastInterface

    @Autowired
    private lateinit var blackListService: BlackListInterface

    @Autowired
    private lateinit var alertService: AlertInterface

    @RequestMapping("/getforecast/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String {

        //verific daca locatia mea imi permite sa caut informatii despre vreme
        //daca se afla afla pe black list atunci nu pot cauta informatii
        val verifyLocation: Boolean = blackListService.isLocationAllowed()
        if(verifyLocation == false)
            return "Nu aveti permisiunea sa cautati date despre vreme!"

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