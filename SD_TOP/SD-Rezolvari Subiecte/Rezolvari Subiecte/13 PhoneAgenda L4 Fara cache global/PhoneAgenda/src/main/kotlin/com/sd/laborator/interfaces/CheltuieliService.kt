package com.sd.laborator.interfaces


interface CheltuieliService {
    fun exists(query: String): String
    fun addToCache(query: String, result: String)
}