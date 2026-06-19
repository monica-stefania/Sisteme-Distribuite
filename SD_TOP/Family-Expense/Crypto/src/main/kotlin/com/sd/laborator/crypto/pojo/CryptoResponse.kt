package com.sd.laborator.crypto.pojo

data class CryptoResponse(
    val success: Boolean,
    val message: String,
    val data: CryptoData? = null
)