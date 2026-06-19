package com.sd.read

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class ReadApplication

fun main(args: Array<String>) {
    runApplication<ReadApplication>(*args)
}