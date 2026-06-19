package com.sd.laborator.crypto.pojo
import kotlinx.serialization.*

@Serializable
data class CryptoData(
    val text: String,
    val encryptedText: String? = null
)
