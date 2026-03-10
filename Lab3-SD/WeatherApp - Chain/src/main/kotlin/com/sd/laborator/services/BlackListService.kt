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
    override fun isLocationAllowed(): Boolean
    {
        //aflare lcatie reala a nodului de calcul folosind IP-ul
        val myAddressURL = URL("http://ip-api.com/json/")
        val addressResponse = myAddressURL.readText()
        val addressRootObject = JSONObject(addressResponse)
        val myCity = addressRootObject.getString("city")

        val list = File("src/main/kotlin/com/sd/laborator/blacklist/blacklist.json").readText()
        val listRootObject = JSONObject(list)
        val blackList = listRootObject.getJSONArray("blacklist")

        for (i in 0 until blackList.length()) {
            val item = blackList.getString(i)
            if(myCity.equals(item, ignoreCase = true)) {
                return false
            }
        }
        return true
    }

    override fun setNext(chain: ChainInterface){
        next = chain
    }

    override fun process(context: WeatherContext): String{
        val verifyLocation = isLocationAllowed()

        if(!verifyLocation)
            return "Nu aveti permisiunea sa cautati date despre vreme!"

        return next.process(context)
    }
}