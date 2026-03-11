package com.sd.laborator.controllers

import com.sd.laborator.interfaces.AlertInterface
import com.sd.laborator.interfaces.BlackListInterface
import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.CoordinatesData
import com.sd.laborator.pojo.WeatherForecastData
import com.sd.laborator.services.WeatherOrchestrator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class WeatherAppOrchestrator {
    @Autowired
    private lateinit var orchestrator: WeatherOrchestrator

    @RequestMapping("/getforecast/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String {
        return orchestrator.getForecastData(location)
    }
}