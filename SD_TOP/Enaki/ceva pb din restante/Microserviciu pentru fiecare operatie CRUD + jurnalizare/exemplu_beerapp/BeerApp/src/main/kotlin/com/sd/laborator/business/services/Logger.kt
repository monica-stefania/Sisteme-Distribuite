package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ILogger
import com.sd.laborator.models.LogData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class Logger: ILogger {
    private lateinit var fileName: String
    // lateinit var fileName: String

    // @Autowired
    // private lateinit var fileName: String

    private fun creteFileIfNotExists() {
        val file = File(fileName)
        if(!file.exists()){
            file.createNewFile()
            file.writeText("[]")
        }
    }

    override fun log(operationType: String) {
        creteFileIfNotExists()
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val formattedData = current.format(formatter)
        val newLog = LogData(operationType, formattedData)
        println("Logging: $newLog")
        val logsFile = File(fileName)
        val logs = Json.decodeFromString<MutableList<LogData>>(logsFile.readText())
        logs += newLog
        logsFile.writeText(Json.encodeToString(logs))
    }

    override fun setFileName(fileName: String) {
        this.fileName = fileName
    }
}