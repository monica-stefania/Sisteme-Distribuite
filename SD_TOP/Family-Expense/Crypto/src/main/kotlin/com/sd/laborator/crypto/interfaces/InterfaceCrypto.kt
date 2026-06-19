package com.sd.laborator.crypto.interfaces

import com.sd.laborator.crypto.pojo.CryptoData
import com.sd.laborator.crypto.pojo.CryptoResponse

interface InterfaceCrypto {
    fun encrypt(text: String): CryptoResponse
    fun decrypt(encryptedText: String): CryptoResponse
}