package com.sd.laborator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Hello {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<Hello>(*args)
        }
    }
}


