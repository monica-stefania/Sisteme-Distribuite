package com.sd.laborator.pojo

data class WeatherContext(
    val locationName: String,
    var coords: CoordinatesData? = null,
    var weatherData: WeatherForecastData? = null)