package com.sd.laborator.interfaces

import com.sd.laborator.pojo.WeatherContext

interface ChainInterface {
    fun setNext(chain: ChainInterface)
    fun process(context: WeatherContext): String
}