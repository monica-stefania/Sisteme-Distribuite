package com.sd.update.repositories

import com.sd.update.models.Beer
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
open class UpdateBeerRepository(
    private val jdbcTemplate: JdbcTemplate
) : IUpdateBeerRepository {

    override fun update(beer: Beer) {
        jdbcTemplate.update(
            "UPDATE beers SET name = ?, price = ? WHERE id = ?",
            beer.beerName,
            beer.beerPrice,
            beer.beerID
        )
    }
}