package com.sd.laborator.controllers
import com.sd.laborator.interfaces.*
import com.sd.laborator.pojo.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
class AgendaController {

    // @@@ AICI SE POATE PUNE SI SERVICIUL CACHE CU SQL @@@

    @Autowired
    private lateinit var agendaService: AgendaService

    @Autowired
    private lateinit var cacheAgendaService: cacheAgendaService

    @RequestMapping(value = ["/person"], method = [RequestMethod.POST])
    fun createPerson(@RequestBody person: Person): ResponseEntity<Unit> {
        agendaService.createPerson(person)
        cacheAgendaService.createPerson(person)
        return ResponseEntity(Unit, HttpStatus.CREATED)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.GET])
    fun getPerson(@PathVariable id: Int): ResponseEntity<Person?> {
        var person: Person? = cacheAgendaService.getPerson(id)

        var status = if (person == null) { HttpStatus.NOT_FOUND } else { HttpStatus.OK }

        if(status == HttpStatus.OK) {
            println("Cache called")
            return ResponseEntity(person, status)
        }
        else {
            person = agendaService.getPerson(id)
            status = if (person == null) { HttpStatus.NOT_FOUND } else { HttpStatus.OK }

            //daca s-a gasit in storage, il punem si in cache, pentru a-l putea accesa rapid
            if(status == HttpStatus.OK) {
                println("Not found in cache, but creating new record.")
                person!!.date_created = LocalDateTime.now() // il luam din storage si trebuie sa ii schimbam data.
                cacheAgendaService.createPerson(person)
            }
            return ResponseEntity(person, status)
        }
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.PUT])
    fun updatePerson(@PathVariable id: Int, @RequestBody person: Person): ResponseEntity<Unit> {
        agendaService.getPerson(id)?.let {
            agendaService.updatePerson(it.id, person)
            cacheAgendaService.updatePerson(it.id, person)
            return ResponseEntity(Unit, HttpStatus.ACCEPTED)
        } ?: return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
    }

    @RequestMapping(value = ["/person/{id}"], method = [RequestMethod.DELETE])
    fun deletePerson(@PathVariable id: Int): ResponseEntity<Unit> {
        if (agendaService.getPerson(id) != null) {
            agendaService.deletePerson(id)
            cacheAgendaService.deletePerson(id)
            return ResponseEntity(Unit, HttpStatus.OK)
        } else {
            return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/updatepersonmoney/{id}/{money}"], method = [RequestMethod.PUT])
    fun updateMoney(@PathVariable id: Int, @PathVariable money: Int): ResponseEntity<Unit> {
        if (agendaService.getPerson(id) != null) {
            val newPerson = Person(id, agendaService.getPerson(id)!!.lastName, agendaService.getPerson(id)!!.firstName, agendaService.getPerson(id)!!.telephoneNumber, money)
            agendaService.updatePerson(id, newPerson)
            cacheAgendaService.updatePerson(id, newPerson)
            return ResponseEntity(Unit, HttpStatus.OK)
        } else {
            return ResponseEntity(Unit, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = ["/agenda"], method = [RequestMethod.GET])
    fun search(@RequestParam(required = false, name = "lastName", defaultValue = "") lastName: String,
                    @RequestParam(required = false, name = "firstName", defaultValue = "") firstName: String,
                     @RequestParam(required = false, name = "telephone", defaultValue = "") telephoneNumber: String):
            ResponseEntity<List<Person>> {
        val personList = agendaService.searchAgenda(lastName, firstName, telephoneNumber)
        var httpStatus = HttpStatus.OK
        if (personList.isEmpty()) {
            httpStatus = HttpStatus.NO_CONTENT
        }
        return ResponseEntity(personList, httpStatus)
    }
}