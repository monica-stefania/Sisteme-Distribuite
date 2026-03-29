package com.laborator.sd.CacheMicroservice.persistence.interfaces

import com.laborator.sd.CacheMicroservice.persistence.entitites.CacheEntity

interface ICachingRepository {
    fun createTable()
    fun getByQuery(query: String): CacheEntity?
    fun add(item: CacheEntity)
    fun update(item: CacheEntity)
}