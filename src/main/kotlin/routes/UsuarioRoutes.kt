package com.eventos.routes

import com.eventos.model.Usuario
import com.eventos.repository.UsuarioRepository
import com.eventos.service.JwtService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Application.usuarioRoutes() {
    val repository = UsuarioRepository()

    routing {
        route("/usuarios") {

            post {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                if (token == null || !JwtService.verifyToken(token)) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                    return@post
                }

                val usuarioRequest = call.receive<Usuario>()
                val created = repository.create(usuarioRequest)

                if (created != null) {
                    call.respond(HttpStatusCode.Created, created.copy(senha = ""))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Erro ao criar usuário"))
                }
            }

            get {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                if (token == null || !JwtService.verifyToken(token)) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                    return@get
                }

                call.respond(repository.all().map { it.copy(senha = "") })
            }

            get("/{id}") {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                if (token == null || !JwtService.verifyToken(token)) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                    return@get
                }

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@get
                }

                val usuario = repository.findById(id)?.copy(senha = "")
                if (usuario == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuário não encontrado"))
                } else {
                    call.respond(usuario)
                }
            }

            put("/{id}") {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                if (token == null || !JwtService.verifyToken(token)) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                    return@put
                }

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@put
                }

                val usuarioRequest = call.receive<Usuario>()
                val updated = repository.update(id, usuarioRequest)

                if (!updated) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuário não encontrado"))
                    return@put
                }

                val usuarioAtualizado = repository.findById(id)?.copy(senha = "")

                call.respond(usuarioAtualizado!!)
            }

            delete("/{id}") {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")?.trim()
                if (token == null || !JwtService.verifyToken(token)) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Unauthorized"))
                    return@delete
                }

                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID inválido"))
                    return@delete
                }

                val deleted = repository.delete(id)

                if (deleted) {
                    call.respond(mapOf("message" to "Usuário deletado com sucesso"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Usuário não encontrado"))
                }
            }
        }
    }
}
