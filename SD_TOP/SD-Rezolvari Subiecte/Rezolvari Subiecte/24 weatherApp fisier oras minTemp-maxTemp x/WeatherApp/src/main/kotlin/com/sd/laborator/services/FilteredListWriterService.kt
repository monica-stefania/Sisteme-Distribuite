package com.sd.laborator.services

import com.sd.laborator.interfaces.FilterInterface
import com.sd.laborator.interfaces.FilteredListWriterServiceInterface
import com.sd.laborator.pojo.WeatherForecastData
import org.springframework.stereotype.Service
import java.io.File

@Service
class FilteredListWriterService(var locationSearchService: LocationSearchService, var weatherForecastService: WeatherForecastService):
    FilteredListWriterServiceInterface // mediator (dirijor) - foloseste serviciile locationSearch, weatherForecast si cityFileWriter
{
    val filtersList: List<FilterInterface> = listOf(MaxTempFilter()) // DE ADAUGAT FILTRE @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

    override fun writeWithFiltersApplied(location: String) {
            val cityFileWriter = CityFileWriter(location)
            val locationId = locationSearchService.getLocationId(location)

            // dacă locaţia nu a fost găsită, răspunsul va fi corespunzător
            if (locationId == -1) {
                println("Nu s-au putut gasi date meteo pentru cuvintele cheie \"$location\"!")
            }

            val rawForecastData: WeatherForecastData = weatherForecastService.getForecastData(locationId)

            //se verifica fiecare filtru sa fie indeplinit
            var ok = 1
            filtersList.forEach { filter->
                if(!filter.execute(rawForecastData))
                    ok=0
            }

            if(ok==1)
                cityFileWriter.write("${rawForecastData.location} Min:${rawForecastData.minTemp} Max:${rawForecastData.maxTemp}\n")
    }
}