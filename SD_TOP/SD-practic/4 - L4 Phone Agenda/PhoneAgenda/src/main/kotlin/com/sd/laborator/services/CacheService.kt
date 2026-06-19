package com.sd.laborator.services

import com.sd.laborator.interfaces.ICacheService
import com.sd.laborator.pojo.Cache
import com.sd.laborator.pojo.Person
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class CacheService: ICacheService {
    private val cache = ConcurrentHashMap<String, Cache>()
    private val max_time = 30 * 60 * 1000L

    override fun get(key: String): List<Person>?
    {
        val entry = cache[key]

        if (entry == null)
        {
            println("[CACHE] Nu s-a gasit nimic in memorie pentru cheia: $key (Cache Miss)")
            return null
        }
        if (System.currentTimeMillis() - entry.timestamp > max_time) {
            println("⏰ [CACHE EXPIRED] Cheia $key a expirat!")
            cache.remove(key)
            return null
        }
        println("[CACHE HIT] Returnăm datele direct din cache pentru: $key")
        return entry.response
    }

    override fun put(key: String, value: List<Person>)
    {
        cache[key] = Cache(key, value)
    }
    override fun delete(key: String)
    {
        cache.remove(key)
    }
    override fun deleteAll()
    {
        cache.clear()
    }
}