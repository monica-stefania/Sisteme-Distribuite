package com.sd.laborator.persistence.repositories

import com.sd.laborator.business.models.Book
import com.sd.laborator.persistence.interfaces.ILibraryRepository
import com.sd.laborator.persistence.mappers.LibraryRowMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.UncategorizedSQLException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

@Repository
open class LibraryRepository : ILibraryRepository{
    @Autowired
    private lateinit var _jdbcTemplate: JdbcTemplate
    private var _rowMapper: RowMapper<Book> = LibraryRowMapper()
    override fun createTable() {
        _jdbcTemplate.execute ( """
            CREATE TABLE IF NOT EXISTS books (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                author VARCHAR(100) NOT NULL,
                title VARCHAR(100) NOT NULL,
                publisher VARCHAR(100) NOT NULL,
                text TEXT UNIQUE);
        """)
    }
    override fun add(book: Book) {
        try{
            _jdbcTemplate.update("INSERT INTO books(author, title, publisher, text ) " +
                    "VALUES (?, ?, ?, ?)", book.author, book.name, book.publisher, book.content)
        } catch(e: UncategorizedSQLException)
        {
            println("An error has occurred in ${this.javaClass.name}.add")
        }
    }

    override fun getAll(): Set<Book> {
        return _jdbcTemplate.query("SELECT * FROM books", _rowMapper).toSet()
    }

    override fun findAllByAuthor(author: String): Set<Book> {
        return _jdbcTemplate.query("SELECT * FROM books WHERE author = ?", _rowMapper, author).toSet()
    }

    override fun findAllByTitle(title: String): Set<Book> {
        return _jdbcTemplate.query("SELECT * FROM books WHERE title = ?", _rowMapper, title).toSet()
    }

    override fun findAllByPublisher(publisher: String): Set<Book> {
        return _jdbcTemplate.query("SELECT * FROM books WHERE publisher = ?", _rowMapper, publisher).toSet()
    }
}