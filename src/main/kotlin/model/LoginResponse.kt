package com.eventos.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String,
    val isAdmin: Boolean,
    val nome: String,
    val email: String
)
