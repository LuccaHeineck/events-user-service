package com.eventos.model

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

@Serializable
data class Usuario(
    val id: Int? = null,
    val isAdmin: Boolean = false,
    val nome: String,
    val email: String,
    val senha: String,  // hash da senha
    val cpf: String? = null,
    val telefone: String? = null
)

object UsuariosTable : Table("usuarios") {
    val id: Column<Int> = integer("id_usuario").autoIncrement()
    val isAdmin = bool("is_admin").default(false)
    val nome = text("nome")
    val email = text("email").uniqueIndex()
    val senha = text("senha")
    val cpf = text("cpf").nullable()
    val telefone = text("telefone").nullable()

    override val primaryKey = PrimaryKey(id)
}
