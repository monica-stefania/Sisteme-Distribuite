package com.sd.laborator.services

import com.sd.laborator.interfaces.ChainInterface
import com.sd.laborator.interfaces.WeatherForecastInterface
import com.sd.laborator.pojo.CoordinatesData
import com.sd.laborator.pojo.WeatherContext
import com.sd.laborator.pojo.WeatherForecastData
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.net.URL
import kotlin.math.roundToInt

@Service
class WeatherForecastService (private val timeService: TimeService) : WeatherForecastInterface, ChainInterface {

    private lateinit var  next: ChainInterface

    override fun getForecastData(coords: CoordinatesData): WeatherForecastData {
        // latidunde si longitude locaţiei nu trebuie codificat, deoarece este numeric
        val forecastDataURL = URL("https://api.open-meteo.com/v1/forecast?latitude=${coords.latitude}&longitude=${coords.longitude}&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,wind_direction_10m&daily=temperature_2m_max,temperature_2m_min")

        // preluare conţinut răspuns HTTP la o cerere GET către URL-ul de mai sus
        val rawResponse: String = forecastDataURL.readText()

        // parsare obiect JSON primit
        val responseRootObject = JSONObject(rawResponse)
        val currentDataObject = responseRootObject.getJSONObject("current")
        val dailyDataObject = responseRootObject.getJSONObject("daily")


        // construire şi returnare obiect POJO care încapsulează datele meteo
        return WeatherForecastData(
            latitude = "${coords.latitude}",
            longitude = "${coords.longitude}",
            date = timeService.getCurrentTime(),
            windDirection = currentDataObject.get("wind_direction_10m").toString(),
            windSpeed = currentDataObject.getFloat("wind_speed_10m").roundToInt(),
            minTemp = dailyDataObject.getJSONArray("temperature_2m_min").getDouble(0).roundToInt(),
            maxTemp = dailyDataObject.getJSONArray("temperature_2m_max").getDouble(0).roundToInt(),
            currentTemp = currentDataObject.getFloat("temperature_2m").roundToInt(),
            humidity = currentDataObject.getFloat("relative_humidity_2m").roundToInt()
        )
    }

    override fun setNext(chain: ChainInterface){
        next = chain
    }
    override fun process(context: WeatherContext): String{

        val data = getForecastData(context.coords!!)
        context.weatherData = data
        return next.process(context)
    }
}