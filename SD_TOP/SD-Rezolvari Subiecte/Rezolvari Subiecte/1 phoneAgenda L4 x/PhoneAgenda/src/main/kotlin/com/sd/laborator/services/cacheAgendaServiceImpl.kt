package com.sd.laborator.services;
import com.sd.laborator.interfaces.cacheAgendaService
import com.sd.laborator.pojo.Person
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

@Service
class cacheAgendaServiceImpl : cacheAgendaService {
    companion object {
        val initialAgenda = arrayOf(
            Person(1, "Hello", "Kotlin", "1234"),
            Person(2, "Hello", "Spring", "5678"),
            Person(3, "Hello", "Microservice", "9101112")
        )
    }

    private val agenda = ConcurrentHashMap<Int, Person>(
        initialAgenda.associateBy { person: Person -> person.id }
    )

    override fun getPerson(id: Int): Person? {
        for(contact in agenda) // verificare cache si stergere daca
        {
            if(contact.value.date_created.until(LocalDateTime.now(), ChronoUnit.MINUTES) >= 1)
                deletePerson(contact.value.id)
        }

        agenda[id]?.date_created = LocalDateTime.now()
        return agenda[id]
    }

    override fun createPerson(person: Person) {
        for(contact in agenda) // verificare cache si stergere daca
        {
            if(contact.value.date_created.until(LocalDateTime.now(), ChronoUnit.MINUTES) >= 1)
                deletePerson(contact.value.id)
        }

        agenda[person.id]?.date_created = LocalDateTime.now()
        agenda[person.id] = person
    }

    override fun deletePerson(id: Int) {
        agenda.remove(id)
    }

    override fun updatePerson(id: Int, person: Person) {
        for(contact in agenda) // verificare cache si stergere daca
        {
            if(contact.value.date_created.until(LocalDateTime.now(), ChronoUnit.MINUTES) >= 1)
                deletePerson(contact.value.id)
        }

        deletePerson(id)
        createPerson(person)
    }
}