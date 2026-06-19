package com.sd.laborator.services

class LinearRegression(var getterService: FilteredListGetterService) {
    fun getSlope(): Double
    {
        val n = getterService.filteredCitiesList.size
        var sumXY: Double = 0.0
        var sumX: Double = 0.0
        var sumY: Double = 0.0
        var sumXsquare: Double = 0.0

        getterService.filteredCitiesList.forEach {
            sumXY += it.minTemp*it.maxTemp
            sumX += it.minTemp
            sumY += it.maxTemp
            sumXsquare += it.minTemp * it.minTemp
        }

        return ((n*sumXY)-(sumX*sumY)) / ((n*sumXsquare)-sumXsquare)
    }
}