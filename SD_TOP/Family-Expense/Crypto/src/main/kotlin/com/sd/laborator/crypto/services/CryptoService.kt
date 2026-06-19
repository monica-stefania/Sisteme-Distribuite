package com.sd.laborator.crypto.services

import com.sd.laborator.crypto.interfaces.InterfaceCrypto
import com.sd.laborator.crypto.pojo.CryptoData
import com.sd.laborator.crypto.pojo.CryptoResponse
import org.springframework.stereotype.Service
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Service
class CryptoService: InterfaceCrypto {

    private val secretKey = "LAB_SD_04_SECRET"
    private val initVector = "LAB_SD_04_IV_123"

    override fun encrypt(text: String): CryptoResponse {
        return try {
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), "AES")
            val ivSpec = IvParameterSpec(initVector.toByteArray())
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)

            val encryptedText = Base64.getEncoder().encodeToString(
                cipher.doFinal(text.toByteArray())
            )
            CryptoResponse(true, "Encrypted successfully", CryptoData(text, encryptedText))
        } catch (e: Exception) {
            CryptoResponse(false, "Encrypted error", CryptoData(text, ""))
        }

    }

    override fun decrypt(encryptedText: String): CryptoResponse {

        return try {
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), "AES")
            val ivSpec = IvParameterSpec(initVector.toByteArray())
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec)

            val decoded = Base64.getDecoder().decode(encryptedText)
            val text = cipher.doFinal(decoded).toString()
            CryptoResponse(true, "Decrypted successfully", CryptoData(text, encryptedText))
        }catch (e: Exception){
            CryptoResponse(false, "Decrypted error", CryptoData("", encryptedText))
        }

    }
}