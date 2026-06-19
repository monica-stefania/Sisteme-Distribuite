package com.sd.laborator.services

import com.sd.laborator.interfaces.CachingDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.sql.SQLException

class QueryResultRowMapper : RowMapper<String> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): String {
        return rs.getString("result")
    }
}

@Service
class CachingDAOService: CachingDAO{

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    private fun createTableIfNotExists(){
        jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS cache(
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        timestamp BIGINT,
                                        query VARCHAR(100),
                                        result VARCHAR(1024))""")
    }

    override fun exists(query: String): String {
        createTableIfNotExists()
        var max_age = System.currentTimeMillis() - 1000*60*60 // One hour
        var result: MutableList<String> = jdbcTemplate.query("SELECT result FROM cache WHERE query='$query' AND timestamp >= $max_age", QueryResultRowMapper())
        if(result.size > 0){
            return result.first()
        }
        return ""
    }

    override fun addToCache(query: String, result: String) {
        createTableIfNotExists()
        jdbcTemplate.update("INSERT INTO cache(timestamp, query, result) VALUES (?, ?, ?)", System.currentTimeMillis(), query, result)
    }

}