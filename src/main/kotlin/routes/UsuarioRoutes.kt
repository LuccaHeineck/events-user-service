package com.eventos.routes

import com.eventos.model.LoginRequest
import com.eventos.model.Usuario
import com.eventos.repository.UsuarioRepository
import com.eventos.service.JwtService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Application.usuarioRoutes() {
    val repository = UsuarioRepository()

    routing {
        route("/usuarios") {
            get {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                if (token == null || !JwtService.verifyToken(token)) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }
                call.respond(repository.all().map { it.copy(senha = "") })
            }

            get("/{id}") {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                if (token == null || !JwtService.verifyToken(token)) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@get
                }
                val usuario = repository.findById(id)?.copy(senha = "")
                if (usuario == null) {
                    call.respond(HttpStatusCode.NotFound, "Usuário não encontrado")
                } else {
                    call.respond(usuario)
                }
            }

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

            put("/{id}") {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                if (token == null || !JwtService.verifyToken(token)) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@put
                }

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@put
                }
                val usuarioRequest = call.receive<Usuario>()
                val updated = repository.update(id, usuarioRequest)
                if (updated) {
                    call.respond("Usuário atualizado com sucesso")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Usuário não encontrado")
                }
            }

            delete("/{id}") {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                if (token == null || !JwtService.verifyToken(token)) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@delete
                }

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "ID inválido")
                    return@delete
                }
                val deleted = repository.delete(id)
                if (deleted) {
                    call.respond("Usuário deletado com sucesso")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Usuário não encontrado")
                }
            }
        }
    }
}
