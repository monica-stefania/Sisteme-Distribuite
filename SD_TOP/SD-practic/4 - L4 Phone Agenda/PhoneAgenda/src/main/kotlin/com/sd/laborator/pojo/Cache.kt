package com.sd.laborator.pojo

data class Cache(
    val key: String,
    val response: List<Person>,
    val timestamp: Long = System.currentTimeMillis()
)