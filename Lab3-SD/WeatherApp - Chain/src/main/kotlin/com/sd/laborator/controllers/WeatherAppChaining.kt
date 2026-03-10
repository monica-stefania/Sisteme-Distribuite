package com.sd.laborator.controllers

import com.sd.laborator.interfaces.AlertInterface
import com.sd.laborator.interfaces.BlackListInterface
import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.CoordinatesData
import com.sd.laborator.pojo.WeatherContext
import com.sd.laborator.pojo.WeatherForecastData
import com.sd.laborator.services.AlertService
import com.sd.laborator.services.BlackListService
import com.sd.laborator.services.LocationSearchService
import com.sd.laborator.services.WeatherForecastService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import javax.annotation.PostConstruct

@Controller
class WeatherAppChaining {
    @Autowired
    private lateinit var locationSearchService: LocationSearchService

    @Autowired
    private lateinit var weatherForecastService: WeatherForecastService

    @Autowired
    private lateinit var blackListService: BlackListService

    @Autowired
    private lateinit var alertService: AlertService

    @PostConstruct
    fun setupChain() {
        blackListService.setNext(locationSearchService)
        locationSearchService.setNext(weatherForecastService)
        weatherForecastService.setNext(alertService)
    }

    @RequestMapping("/getforecast/{location}", method = [RequestMethod.GET])
    @ResponseBody
    fun getForecast(@PathVariable location: String): String {
        val context = WeatherContext(location)
        return blackListService.process(context)
    }
}