package com.sd.laborator

data class ChatMessage(
    val senderId: String,
    val type: MessageType,
    val target: String?,
    val content: String
)