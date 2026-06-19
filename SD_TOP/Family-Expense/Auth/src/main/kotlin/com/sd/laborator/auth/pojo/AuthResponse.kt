package com.sd.laborator.auth.pojo

data class AuthResponse(
    var success: Boolean,
    var message: String,
    var memberId: Long? = null,
    var username: String? = null
)
