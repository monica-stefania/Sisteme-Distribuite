package com.sd.laborator.auth.pojo

import kotlinx.serialization.Serializable

@Serializable
data class CryptoResponse(
    var success: Boolean = false,
    var message: String = "",
    var encryptedText: String? = null
)