package com.sd.read.repositories

import com.sd.read.mappers.BeerRowMapper
import com.sd.read.models.Beer
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
open class ReadBeerRepository(
    private val jdbcTemplate: JdbcTemplate
) : IReadBeerRepository {

    override fun getAll(): List<Beer> {
        return jdbcTemplate.query("SELECT * FROM beers", BeerRowMapper())
    }

    override fun getByName(name: String): Beer? {
        return try {
            jdbcTemplate.queryForObject(
                "SELECT * FROM beers WHERE name = ?",
                BeerRowMapper(),
                name
            )
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }
}