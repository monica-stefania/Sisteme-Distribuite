package com.sd.delete.repositories

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
open class DeleteBeerRepository(
    private val jdbcTemplate: JdbcTemplate
) : IDeleteBeerRepository {

    override fun delete(name: String) {
        jdbcTemplate.update(
            "DELETE FROM beers WHERE name = ?",
            name
        )
    }
}