package com.sd.laborator.interfaces


import com.sd.laborator.model.Person

interface DatabaseInterface {
    // Create
    fun createQueryTable()

    // Retrieve
    fun getPersonById(id: Int): Person?
    fun getPersons(): List<Person>
    fun findAllByFirstName(firstName: String): List<Person>
    fun findAllByAge(age: Int): List<Person>

    // Insert
    fun insertPerson(person: Person)

    // Delete
    fun deletePersonByFirstName(firstName: String)



}