package com.sd.laborator

import io.micronaut.runtime.Micronaut

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        // Aceasta pornește tot contextul Micronaut,
        // inclusiv Listenerii RabbitMQ și Controller-ul de intrare.
        Micronaut.run(Application::class.java, *args)
    }
}