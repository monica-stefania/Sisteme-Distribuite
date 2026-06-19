package com.sd.laborator.interfaces

import com.sd.laborator.pojo.Person

interface cacheAgendaService {
    fun getPerson(id: Int) : Person?
    fun createPerson(person: Person)
    fun deletePerson(id: Int)
    fun updatePerson(id: Int, person: Person)
}