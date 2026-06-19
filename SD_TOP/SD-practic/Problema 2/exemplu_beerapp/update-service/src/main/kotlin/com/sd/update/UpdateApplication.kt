package com.sd.update

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class UpdateApplication

fun main(args: Array<String>) {
    runApplication<UpdateApplication>(*args)
}