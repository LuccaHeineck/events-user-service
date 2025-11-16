package com.eventos.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val id: Int? = null,
    val token: String,
    val isAdmin: Boolean,
    val nome: String,
    val email: String,
    val cpf: String? = null,
    val telefone: String? = null
)
