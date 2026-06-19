package com.sd.logging.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.sd.logging.models.LogEntry
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime

@Service
open class LoggingService : ILoggingService {

    private val logFile = File("../logs/execution-log.json")
    private val mapper = jacksonObjectMapper()

    override fun writeLog(entry: LogEntry): String {
        if (!logFile.parentFile.exists()) {
            logFile.parentFile.mkdirs()
        }

        if (!logFile.exists()) {
            logFile.writeText("[]")
        }

        entry.timestamp = LocalDateTime.now().toString()

        val logs: MutableList<LogEntry> = mapper.readValue(logFile)
        logs.add(entry)

        logFile.writeText(
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(logs)
        )

        return "Log written"
    }
}