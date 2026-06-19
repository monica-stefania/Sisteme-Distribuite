package com.sd.logging.controllers

import com.sd.logging.models.LogEntry
import com.sd.logging.services.ILoggingService
import org.springframework.web.bind.annotation.*

@RestController
open class LoggingController(
    private val service: ILoggingService
) {
    @PostMapping("/log")
    fun log(@RequestBody entry: LogEntry): String {
        return service.writeLog(entry)
    }
}