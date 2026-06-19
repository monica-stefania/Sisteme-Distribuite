package com.sd.create.repositories

import com.sd.create.models.Beer
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class CreateBeerRepository(
    private val jdbcTemplate: JdbcTemplate
) : ICreateBeerRepository {

    override fun createTable() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS beers(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name VARCHAR(100) UNIQUE,
                price FLOAT
            )
        """.trimIndent())
    }

    override fun add(beer: Beer) {
        jdbcTemplate.update(
            "INSERT INTO beers(name, price) VALUES (?, ?)",
            beer.beerName,
            beer.beerPrice
        )
    }
}