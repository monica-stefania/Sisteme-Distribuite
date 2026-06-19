package com.sd.laborator.services

import com.sd.laborator.interfaces.CityFileReaderInterface
import java.io.File

class CityFileReader : CityFileReaderInterface {
    var cities: MutableList<String> = mutableListOf()

    override fun read(file: File): MutableList<String> {
        file.forEachLine { cities.add(it) }
        return cities
    }
}