package com.laborator.sd.CacheMicroservice.business.services

import com.laborator.sd.CacheMicroservice.business.interfaces.ICachingService
import com.laborator.sd.CacheMicroservice.business.models.CacheModel
import com.laborator.sd.CacheMicroservice.persistence.entitites.CacheEntity
import com.laborator.sd.CacheMicroservice.persistence.interfaces.ICachingRepository
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.swing.text.html.parser.Entity

@Service
class CachingService: ICachingService
{
    @Autowired
    private lateinit var _cachingRepository: ICachingRepository

    @PostConstruct
    fun init()
    {
        _cachingRepository.createTable()
    }

    override fun exists(query: String): CacheModel? {
        val entity = _cachingRepository.getByQuery(query)

        val now = System.currentTimeMillis() / 1000
        if (entity != null) {
            if(now - entity.cacheTimestamp <= 3600)
            {
                print("Cache HIT for query: $query")
                return CacheModel.FromEntity(entity)
            }
            else
            {
                print("Cache HIT for query, but time expired: $query")
                return null
            }
        }
        print("Cache MISS for query: $query")
        return null

    }
    override fun addToCache(query: String, result: String) {
        if (_cachingRepository.getByQuery(query) != null)
        {
            //exista deci fac un update
           updateCache(query, result)
        }
        else {
            val entity = CacheEntity(0, (System.currentTimeMillis() / 1000).toInt(), query, result)
            _cachingRepository.add(entity)
        }
    }

    override fun updateCache(query: String, result: String) {
        val entity = CacheEntity(
            0,
            (System.currentTimeMillis() / 1000).toInt(),
            query,
            result
        )
        _cachingRepository.update(entity)
    }
}