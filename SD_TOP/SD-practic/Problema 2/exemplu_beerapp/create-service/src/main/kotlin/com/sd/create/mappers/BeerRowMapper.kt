package com.sd.create.mappers

import com.sd.create.models.Beer
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

class BeerRowMapper : RowMapper<Beer> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Beer {
        return Beer(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getFloat("price")
        )
    }
}