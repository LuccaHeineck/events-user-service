package com.eventos.routes

import com.eventos.model.LoginRequest
import com.eventos.model.Usuario
import com.eventos.repository.UsuarioRepository
import com.eventos.service.JwtService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Application.authRoutes() {
    val repository = UsuarioRepository()

    routing {
        route("/auth") {
            post("/register") {
                val usuarioRequest = call.receive<Usuario>()
                val usuarioCriado = repository.create(usuarioRequest)
                call.respond(usuarioCriado.copy(senha = ""))
            }

            post("/login") {
                val loginRequest = call.receive<LoginRequest>()
                if (repository.verifyLogin(loginRequest.email, loginRequest.senha)) {
                    val token = JwtService.generateToken(loginRequest.email)
                    call.respond(mapOf("token" to token))
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Email ou senha inválidos")
                }
            }
        }
    }
}
