package com.laborator.sd.CacheMicroservice.persistence.mappers
import com.laborator.sd.CacheMicroservice.persistence.entitites.CacheEntity
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet
import java.sql.SQLException

class CacheRowMapper: RowMapper<CacheEntity> {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): CacheEntity
    {
        return CacheEntity(rs.getInt("id"), rs.getInt("timestamp"), rs.getString("query"), rs.getString("result"))
    }
}