package com.sd.laborator.services

import com.sd.laborator.interfaces.BlackListInterface
import com.sd.laborator.interfaces.ChainInterface
import com.sd.laborator.pojo.CoordinatesData
import com.sd.laborator.pojo.WeatherContext
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.io.File
import java.net.URL

@Service
class BlackListService: BlackListInterface, ChainInterface{
    private lateinit var next : ChainInterface
    override fun isLocationAllowed(location: String): Boolean
    {
        val list = File("src/main/kotlin/com/sd/laborator/blacklist/blacklist.json").readText()
        val listRootObject = JSONObject(list)
        val blackList = listRootObject.getJSONArray("blacklist")

        for (i in 0 until blackList.length()) {
            val item = blackList.getString(i)
            if(location.equals(item, ignoreCase = true)) {
                return false
            }
        }
        return true
    }

    override fun setNext(chain: ChainInterface){
        next = chain
    }

    override fun process(context: WeatherContext): String{
        val verifyLocation = isLocationAllowed(context.locationName)

        if(!verifyLocation)
            return "Nu sunt date disponibile pentru aceasta locatie {$context.locationName}!"

        return next.process(context)
    }
}