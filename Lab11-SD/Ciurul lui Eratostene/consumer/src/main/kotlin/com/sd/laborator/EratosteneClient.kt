package com.sd.laborator

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:8080")
interface EratosteneClient {

    @Post("/")
    fun calculatePrimes(@Body request: EratosteneRequest): EratosteneResponse

}