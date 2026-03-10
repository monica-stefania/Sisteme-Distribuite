package com.sd.laborator.pojo

data class WeatherForecastData (
    var latitude: String,
    var longitude: String,
    var date: String,
    var windDirection: String,
    var windSpeed: Int, // km/h
    var minTemp: Int, // grade celsius
    var maxTemp: Int,
    var currentTemp: Int,
    var humidity: Int // procent
)
