package com.sd.logging.services

import com.sd.logging.models.LogEntry

interface ILoggingService {
    fun writeLog(entry: LogEntry): String
}