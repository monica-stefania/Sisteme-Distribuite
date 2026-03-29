package com.laborator.sd.CacheMicroservice.business.interfaces

import com.laborator.sd.CacheMicroservice.business.models.CacheModel
import com.laborator.sd.CacheMicroservice.persistence.entitites.CacheEntity

interface ICachingService {
    fun exists(query: String): CacheModel?
    fun addToCache(query: String, result: String)
    fun updateCache(query: String, result: String)
}