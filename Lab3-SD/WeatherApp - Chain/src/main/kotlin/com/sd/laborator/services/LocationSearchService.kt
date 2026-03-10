package com.sd.laborator.services
import com.sd.laborator.interfaces.ChainInterface
import com.sd.laborator.interfaces.LocationSearchInterface
import com.sd.laborator.pojo.CoordinatesData
import com.sd.laborator.pojo.WeatherContext
import org.springframework.stereotype.Service
import java.net.URL
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Service
class LocationSearchService : LocationSearchInterface, ChainInterface{
    private lateinit var next: ChainInterface

    override fun getLocationLatLon(locationName: String): CoordinatesData {
        // codificare parametru URL (deoarece poate conţine caractere speciale)
        val encodedLocationName = URLEncoder.encode(locationName, StandardCharsets.UTF_8.toString())

        // construire obiect de tip URL
        val locationSearchURL = URL("https://geocoding-api.open-meteo.com/v1/search?name=$encodedLocationName")

        // preluare raspuns HTTP (se face cerere GET şi se preia conţinutul răspunsului sub formă de text)
        val rawResponse: String = locationSearchURL.readText()

        // parsare obiect JSON
        val responseRootObject = JSONObject(rawResponse)
        val responseContentObject = responseRootObject.getJSONArray("results")
        if (responseContentObject != null && responseContentObject.length() > 0)
        {
            val latitude = responseContentObject.getJSONObject(0).getDouble("latitude")
            val longitude = responseContentObject.getJSONObject(0).getDouble("longitude")
            return CoordinatesData(latitude, longitude)
        }
        return CoordinatesData(0.0, 0.0)
    }

    override fun setNext(chain: ChainInterface) {
        next = chain
    }

    override fun process(context: WeatherContext): String{
        context.coords = getLocationLatLon(context.locationName)
        if(context.coords!!.latitude == 0.0 || context.coords!!.longitude == 0.0)
            return "Nu s-au putut gasi date meteo pentru \"${context.locationName}\"!"

        return next.process(context)
    }
}