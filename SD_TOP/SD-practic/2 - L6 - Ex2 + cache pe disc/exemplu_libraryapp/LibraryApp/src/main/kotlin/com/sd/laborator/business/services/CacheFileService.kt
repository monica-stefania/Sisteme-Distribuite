package com.sd.laborator.business.services

import com.fasterxml.jackson.databind.util.JSONPObject
import com.sd.laborator.business.interfaces.ICacheFileService
import com.sd.laborator.business.models.Cache
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.io.File

@Service
class CacheFileService: ICacheFileService {
    private val cacheFile = File("cache_store.txt")

    init{
        if(!cacheFile.exists())
            cacheFile.createNewFile()
    }
    override fun addToCache(cache: Cache) {
        val rezultat_curatat = cache.result.replace("\n", " ").replace("\r", " ")
        val text = "${cache.query}~${rezultat_curatat}~${cache.timestamp}\n"
        cacheFile.appendText(text)
        println("S-a adaugat in fisier interogarea: ${cache.query}")
    }

    override fun getValidCache(query: String): Cache? {
        var linie_gasita: String? = null
        cacheFile.forEachLine { line ->
            if (line.isNotBlank()) {
                val parts = line.split('~', limit = 3)

                if (parts[0] == query) {
                    linie_gasita = line
                }
            }
        }
        if (linie_gasita != null) {
            val parts = linie_gasita!!.split('~', limit = 3)
            val query_file = parts[0]
            val result_file = parts[1]
            val timestamp_file = parts[2].trim().toLong()

            val max_time = 60 * 60 * 1000

            if (System.currentTimeMillis() - timestamp_file < max_time) {
                println("[CACHE HIT] Datele sunt valide sub limita de o ora.")
                return Cache(query_file, result_file, timestamp_file)
            } else {
                println("[CACHE EXPIRED] Datele din cache au expirat.")
            }
        }
        return null
    }
}