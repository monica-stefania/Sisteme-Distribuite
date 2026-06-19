package com.sd.logging.models

data class LogEntry(
    var operation: String = "",
    var details: String = "",
    var timestamp: String = ""
)