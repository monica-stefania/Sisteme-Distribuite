package com.sd.laborator.crypto.controllers

import com.sd.laborator.crypto.interfaces.InterfaceCrypto
import com.sd.laborator.crypto.pojo.CryptoData
import com.sd.laborator.crypto.pojo.CryptoResponse
import com.sd.laborator.crypto.services.CryptoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/crypto")
class CryptoController {
    @Autowired
    private lateinit var cryptoService: InterfaceCrypto

   @GetMapping("/encrypt")
   fun encrypt(@RequestParam text: String): ResponseEntity<CryptoResponse>
   {
       if(text.isBlank())
           return ResponseEntity(CryptoResponse(false, "The text should not be blank"), HttpStatus.BAD_REQUEST)
       val response = cryptoService.encrypt(text)
       val status = if (response.success) HttpStatus.OK else HttpStatus.INTERNAL_SERVER_ERROR
       return ResponseEntity(response, status)
   }

    @GetMapping("/decrypt")
    fun decrypt(@RequestParam encryptedText: String): ResponseEntity<CryptoResponse>
    {
        if(encryptedText.isBlank())
            return ResponseEntity(CryptoResponse(false, "The text should not be blank"), HttpStatus.BAD_REQUEST)
        val response = cryptoService.decrypt(encryptedText)
        val status = if (response.success) HttpStatus.OK else HttpStatus.INTERNAL_SERVER_ERROR
        return ResponseEntity(response, status)
    }
}