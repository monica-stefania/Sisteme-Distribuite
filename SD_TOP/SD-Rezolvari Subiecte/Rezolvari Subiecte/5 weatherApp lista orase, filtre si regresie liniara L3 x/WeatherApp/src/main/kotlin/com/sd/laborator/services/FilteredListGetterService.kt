package com.sd.laborator.services

import com.sd.laborator.interfaces.FilterInterface
import com.sd.laborator.interfaces.GetterServiceInterface
import com.sd.laborator.pojo.WeatherForecastData
import java.io.File

class FilteredListGetterService(var locationSearchService: LocationSearchService, var weatherForecastService: WeatherForecastService):
    GetterServiceInterface
{
    val fileToRead = File("/home/stefan/Documents/Rezolvari Subiecte/5/cities.txt")
    val citiesList = CityFileReader().read(fileToRead)

    val filteredCitiesList: MutableList<WeatherForecastData> = mutableListOf()
    val filtersList: List<FilterInterface> = listOf() // DE ADAUGAT FILTRE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    override fun get(): String {
        citiesList.forEach{location->
            val locationId = locationSearchService.getLocationId(location)

            // dacă locaţia nu a fost găsită, răspunsul va fi corespunzător
            if (locationId == -1) {
                return "Nu s-au putut gasi date meteo pentru cuvintele cheie \"$location\"!"
            }

            val rawForecastData: WeatherForecastData = weatherForecastService.getForecastData(locationId)

            //se verifica fiecare filtru sa fie indeplinit
            var ok = 1
            filtersList.forEach { filter->
                if(!filter.execute(rawForecastData))
                    ok=0
            }

            if(ok==1)
                filteredCitiesList.add(rawForecastData)
        }

        return filteredCitiesList.toString()
    }
}