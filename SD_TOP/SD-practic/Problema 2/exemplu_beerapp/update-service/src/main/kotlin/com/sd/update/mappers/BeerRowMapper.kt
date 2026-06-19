package com.sd.update.mappers

import com.sd.update.models.Beer
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

open class BeerRowMapper : RowMapper<Beer> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Beer {
        return Beer(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getFloat("price")
        )
    }
}