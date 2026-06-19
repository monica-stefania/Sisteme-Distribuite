package com.sd.laborator

import io.micronaut.function.FunctionBean
import io.micronaut.function.executor.FunctionInitializer
import jakarta.inject.Inject
import java.util.function.Function

@FunctionBean("intersection")
class IntersectionFunction : FunctionInitializer(),
    Function<IntersectionRequest, IntersectionResponse> {

    @Inject
    private lateinit var intersectionService: IntersectionService

    override fun apply(request: IntersectionRequest): IntersectionResponse {
        val response = IntersectionResponse()

        if (request.size <= 0) {
            response.message = "Dimensiunea trebuie sa fie pozitiva."
            return response
        }

        if (request.minValue > request.maxValue) {
            response.message = "minValue trebuie sa fie mai mic sau egal cu maxValue."
            return response
        }

        val a = intersectionService.generateADT(
            request.size,
            request.minValue,
            request.maxValue
        )

        val b = intersectionService.generateADT(
            request.size,
            request.minValue,
            request.maxValue
        )

        val c = intersectionService.intersection(a, b)

        response.message = "Intersectia a fost calculata cu succes."
        response.a = a
        response.b = b
        response.c = c

        return response
    }
}