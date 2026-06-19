package com.sd.laborator.auth.controllers

import com.sd.laborator.auth.pojo.AuthResponse
import com.sd.laborator.auth.pojo.LoginData
import com.sd.laborator.auth.pojo.RegisterData
import com.sd.laborator.auth.services.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["http://localhost:5000"])
@RestController
@RequestMapping("/auth")
class AuthController {
    @Autowired
    private lateinit var authService: AuthService

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterData) : AuthResponse{
        return authService.register(request)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginData) : AuthResponse{
        return authService.login(request)
    }
}