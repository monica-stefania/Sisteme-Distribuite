package com.sd.laborator.services

import com.sd.laborator.interfaces.CityFileWriterInterface
import org.springframework.stereotype.Service
import java.io.File

class CityFileWriter(filename: String) : CityFileWriterInterface {

    private val file = File("/home/stefan/Documents/Rezolvari Subiecte/24 weatherApp/WeatherApp/$filename.txt")
    override fun write(data: String) {
        file.appendText(data)
    }
}