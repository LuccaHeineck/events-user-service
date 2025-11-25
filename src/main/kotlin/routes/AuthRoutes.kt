package com.eventos.routes

import com.eventos.model.LoginRequest
import com.eventos.model.LoginResponse
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
                val usuario = repository.findByEmail(loginRequest.email)

                if (usuario != null && repository.verifyLogin(loginRequest.email, loginRequest.senha)) {
                    val token = JwtService.generateToken(loginRequest.email)
                    val response = LoginResponse(
                        id = usuario.id,
                        token = token,
                        isAdmin = usuario.isAdmin,
                        nome = usuario.nome,
                        email = usuario.email,
                        cpf = usuario.cpf,
                        telefone = usuario.telefone
                    )
                    call.respond(response)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Email ou senha inválidos")
                }
            }

            get("/verify") {
                val authHeader = call.request.headers["Authorization"]

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    call.respond(HttpStatusCode.Unauthorized, "Token ausente ou inválido")
                    return@get
                }

                val token = authHeader.removePrefix("Bearer ").trim()

                if (JwtService.verifyToken(token)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.Unauthorized, "Token inválido")
                }
            }
        }
    }
}
