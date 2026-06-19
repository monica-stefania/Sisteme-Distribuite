package com.sd.laborator.auth.services

import com.sd.laborator.auth.interfaces.InterfaceAuth
import com.sd.laborator.auth.interfaces.MemberRepository
import com.sd.laborator.auth.pojo.AuthResponse
import com.sd.laborator.auth.pojo.CryptoResponse
import com.sd.laborator.auth.pojo.LoginData
import com.sd.laborator.auth.pojo.Member
import com.sd.laborator.auth.pojo.RegisterData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class AuthService: InterfaceAuth
{
    @Autowired
    private lateinit var memberRepository: MemberRepository

    private val restTemplate = RestTemplate()
    private val cryptoUrl = "http://localhost:8082/crypto"

    override fun register(data: RegisterData): AuthResponse {
        if(memberRepository.existsByUsername(data.username)){
            return AuthResponse(false, "Username already exists")
        }

        val encryptedFirstName = encryptText(data.firstName).toString()
        val encryptedLastName = encryptText(data.lastName).toString()

        val hashedPassword = (data.username + data.password).hashCode().toString()

        val member = Member(
            username = data.username,
            password = hashedPassword,
            firstName = encryptedFirstName,
            lastName = encryptedLastName
        )

        val saved = memberRepository.save(member)

        return AuthResponse(true, "User created with success!", saved.id, saved.username)
    }
    override fun login(data: LoginData): AuthResponse {

        if(!memberRepository.existsByUsername(data.username)){
            return AuthResponse(false, "Username not exists! Please create a new user.")
        }

        val member = memberRepository.findByUsername(data.username)

        if(member != null) {
            val hashedPassword = (data.username + data.password).hashCode().toString()

            if (member.password != hashedPassword) {
                return AuthResponse(false, "Password do not match!")
            } else
                return AuthResponse(true, "Login successful", member.id, member.username)
        }

        return AuthResponse(false,"Invalid username or password!")
    }

    fun encryptText(text: String): String? {
        val url = UriComponentsBuilder
            .fromUriString("$cryptoUrl/encrypt")
            .queryParam("text", text)
            .toUriString()

        val response = restTemplate.getForObject(url, CryptoResponse::class.java)
        return if (response != null && response.success) response.encryptedText else null
    }
}