package com.sd.logging

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class LoggingApplication

fun main(args: Array<String>) {
    runApplication<LoggingApplication>(*args)
}