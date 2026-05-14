package com.sd.laborator

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import jakarta.inject.Inject

@Controller("/clicks")
class ClickController {
    @Inject
    private lateinit var clickProducer: ClickProducer

    @Post("/send")
    fun sendClickQueue(@Body click: ClickButton): String{
        println("Am primit apasare pentru ${click.buttonName}")
        clickProducer.sendClick(click)
        return "Mesaj trimis in coada cu succes"
    }
}