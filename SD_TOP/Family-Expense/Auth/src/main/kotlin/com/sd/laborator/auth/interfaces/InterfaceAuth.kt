package com.sd.laborator.auth.interfaces

import com.sd.laborator.auth.pojo.AuthResponse
import com.sd.laborator.auth.pojo.LoginData
import com.sd.laborator.auth.pojo.RegisterData

interface InterfaceAuth {
    fun register(request: RegisterData): AuthResponse
    fun login(request: LoginData): AuthResponse
}