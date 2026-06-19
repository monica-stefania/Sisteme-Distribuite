package com.sd.laborator.business.models

data class Cache(
    val query: String,
    val result: String,
    val timestamp: Long
)
