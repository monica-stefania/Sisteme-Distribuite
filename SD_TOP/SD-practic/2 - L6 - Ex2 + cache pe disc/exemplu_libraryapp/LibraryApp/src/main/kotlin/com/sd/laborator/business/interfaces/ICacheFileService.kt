package com.sd.laborator.business.interfaces

import com.sd.laborator.business.models.Cache

interface ICacheFileService {
    fun addToCache(cache: Cache)
    fun getValidCache(query: String): Cache?
}