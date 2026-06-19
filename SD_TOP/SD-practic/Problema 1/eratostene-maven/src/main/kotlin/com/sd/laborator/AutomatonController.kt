package com.sd.laboratory

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject

@Controller("/automaton")
class AutomatonController {

    @Inject
    lateinit var queue: AutomatonMessageQueue

    @Post("/{n}")
    fun submit(@PathVariable n: Int): HttpResponse<Map<String, Any>> {
        queue.send(AutomatonMessage(n))
        return HttpResponse.ok(mapOf("status" to "queued", "input" to n))
    }
}