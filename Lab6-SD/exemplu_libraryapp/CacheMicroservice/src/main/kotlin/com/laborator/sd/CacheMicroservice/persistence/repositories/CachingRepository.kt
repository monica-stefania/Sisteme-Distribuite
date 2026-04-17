package com.laborator.sd.CacheMicroservice.persistence.repositories

import com.laborator.sd.CacheMicroservice.persistence.entitites.CacheEntity
import com.laborator.sd.CacheMicroservice.persistence.interfaces.ICachingRepository
import com.laborator.sd.CacheMicroservice.persistence.mappers.CacheRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import javax.swing.tree.RowMapper

@Repository
class CachingRepository: ICachingRepository {

    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _mapper = CacheRowMapper()

    override fun createTable() {
        _jdbcTemplate.execute(
            """
                CREATE TABLE IF NOT EXISTS caches(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp INTEGER NOT NULL,
                    query VARCHAR(100) UNIQUE,
                    result TEXT);
            """
        )
    }

    override fun add(item: CacheEntity) {
        _jdbcTemplate.update(
            "INSERT INTO caches(timestamp, query, result) VALUES (?, ?, ?)",
            item.cacheTimestamp, item.cacheQuery, item.cacheResult
        )
    }

    override fun getByQuery(query: String): CacheEntity? {
        return try {
            _jdbcTemplate.queryForObject(
                "SELECT * FROM caches WHERE query = ?",
                _mapper,
                query
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    override fun update(item: CacheEntity) {
        _jdbcTemplate.update(
            "UPDATE caches SET timestamp = ?, result = ? WHERE query = ?",
            item.cacheTimestamp, item.cacheResult, item.cacheQuery
        )
    }
}