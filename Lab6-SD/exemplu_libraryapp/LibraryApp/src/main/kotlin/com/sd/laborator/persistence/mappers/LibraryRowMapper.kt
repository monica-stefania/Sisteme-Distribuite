package com.sd.laborator.persistence.mappers

import com.sd.laborator.business.models.Book
import com.sd.laborator.business.models.Content
import java.sql.ResultSet
import java.sql.SQLException
import org.springframework.jdbc.core.RowMapper

class LibraryRowMapper : RowMapper<Book> {

    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): Book
    {
        return Book(Content(rs.getString("author"), rs.getString("title"), rs.getString("publisher"), rs.getString("text")))
    }
}