package com.sd.laborator.services

import com.sd.laborator.interfaces.DatabaseInterface
import com.sd.laborator.model.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Service
import java.sql.ResultSet
import java.sql.SQLException
import java.util.regex.Pattern

class PersonRowMapper : RowMapper<Person> {
    @Throws(SQLException::class)
    override fun mapRow(rs: ResultSet, rowNum: Int): Person {
        return Person(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getInt("age"), rs.getString("timestamp"))
    }
}

@Service
class DatabaseService: DatabaseInterface {
    //Spring Boot will automatically wire this object using application.properties:
    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate
    var pattern: Pattern = Pattern.compile("\\W")

    override fun createQueryTable() {
        jdbcTemplate.execute("""CREATE TABLE IF NOT EXISTS "person" ( `id` INTEGER PRIMARY KEY AUTOINCREMENT, firstName VARCHAR, lastName VARCHAR, age INTEGER, timestamp datetime, UNIQUE(firstName, lastName, age) );""")
    }

    override fun getPersonById(id: Int): Person? {
        val result: Person? = jdbcTemplate.queryForObject("SELECT * FROM person WHERE id = '$id';", PersonRowMapper())
        return result
    }

    override fun getPersons(): List<Person> {
        return jdbcTemplate.query("SELECT * FROM person", PersonRowMapper())
    }

    override fun findAllByFirstName(firstName: String): List<Person> {
        if(pattern.matcher(firstName).find()) {
            println("SQL Injection for book author")
            return emptyList()
        }
        return jdbcTemplate.query("SELECT * FROM person WHERE upper(firstName) LIKE '%${firstName.toUpperCase()}%'", PersonRowMapper())
    }

    override fun findAllByAge(age: Int): List<Person> {
        return jdbcTemplate.query("SELECT * FROM person WHERE WHERE age = '$age';", PersonRowMapper())
    }

    override fun insertPerson(person: Person) {
        jdbcTemplate.update("""INSERT OR REPLACE INTO person(firstName, lastName, age, timestamp) VALUES (?, ?, ?, datetime('now', 'localtime'));""", person.firstname, person.lastname, person.age)
    }

    override fun deletePersonByFirstName(firstName: String) {
        jdbcTemplate.update("DELETE FROM person WHERE firstName = ?;", firstName)
    }


}