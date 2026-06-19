package com.sd.laborator.models

import java.time.LocalDateTime

@kotlinx.serialization.Serializable
data class LogData(
    val operationType: String,
    val date: String
)
