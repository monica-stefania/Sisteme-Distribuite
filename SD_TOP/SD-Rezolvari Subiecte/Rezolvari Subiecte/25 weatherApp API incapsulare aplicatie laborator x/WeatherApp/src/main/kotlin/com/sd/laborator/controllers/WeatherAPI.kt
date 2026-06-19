package com.sd.laborator.controllers

import khttp.*
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class WeatherAPI {
    @RequestMapping("/vremea/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String {
        return khttp.get("http://localhost:8080/getforecast/$location").text
    }

    @RequestMapping("/vremeabucuresti", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecastBucharest(): String {
        return khttp.get("http://localhost:8080/getforecast/Bucharest").text
    }

// de adaugat detalii cu minTemp, maxTemp, etc...
}
