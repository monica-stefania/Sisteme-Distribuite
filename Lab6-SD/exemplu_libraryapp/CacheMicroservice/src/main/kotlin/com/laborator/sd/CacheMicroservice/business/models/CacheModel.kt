package com.laborator.sd.CacheMicroservice.business.models

import com.laborator.sd.CacheMicroservice.persistence.entitites.CacheEntity

class CacheModel(private var query: String, private var result: String) {
    var cacheQuery: String
        get()
        {
            return query
        }
        set(value)
        {

            query = value
        }
    var cacheResult: String
        get()
        {
            return result
        }
        set(value)
        {
            result = value
        }

    companion object {
        fun ToEntity(item: CacheModel): CacheEntity {
            return CacheEntity(0, (System.currentTimeMillis() / 1000).toInt(), item.cacheQuery, item.cacheResult)
        }

        fun FromEntity(item: CacheEntity): CacheModel {
            return CacheModel(item.cacheQuery, item.cacheResult)
        }
    }
}