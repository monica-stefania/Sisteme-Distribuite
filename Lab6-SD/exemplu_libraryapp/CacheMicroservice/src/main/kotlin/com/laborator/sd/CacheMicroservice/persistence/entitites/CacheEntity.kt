package com.laborator.sd.CacheMicroservice.persistence.entitites


class CacheEntity(private var id: Int, private var timestamp: Int, private var query: String, private var result: String) {
    var cacheId: Int
        get()
        {
            return id
        }
        set(value)
        {
            id = value
        }
    var cacheTimestamp: Int
        get()
        {
            return timestamp
        }
        set(value)
        {
            timestamp = value
        }
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
}