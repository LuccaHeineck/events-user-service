package com.eventos.repository

import com.eventos.model.Usuario
import com.eventos.model.UsuariosTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class UsuarioRepository {

    fun all(): List<Usuario> = transaction {
        UsuariosTable.selectAll().map {
            Usuario(
                id = it[UsuariosTable.id],
                isAdmin = it[UsuariosTable.isAdmin],
                nome = it[UsuariosTable.nome],
                email = it[UsuariosTable.email],
                senha = it[UsuariosTable.senha],
                cpf = it[UsuariosTable.cpf],
                telefone = it[UsuariosTable.telefone]
            )
        }
    }

    fun findById(id: Int): Usuario? = transaction {
        UsuariosTable.selectAll()
            .firstOrNull { it[UsuariosTable.id] == id }
            ?.let { row ->
                Usuario(
                    id = row[UsuariosTable.id],
                    isAdmin = row[UsuariosTable.isAdmin],
                    nome = row[UsuariosTable.nome],
                    email = row[UsuariosTable.email],
                    senha = row[UsuariosTable.senha],
                    cpf = row[UsuariosTable.cpf],
                    telefone = row[UsuariosTable.telefone]
                )
            }
    }

    fun findByEmail(email: String): Usuario? = transaction {
        UsuariosTable.selectAll()
            .firstOrNull { it[UsuariosTable.email] == email }
            ?.let { row ->
                Usuario(
                    id = row[UsuariosTable.id],
                    isAdmin = row[UsuariosTable.isAdmin],
                    nome = row[UsuariosTable.nome],
                    email = row[UsuariosTable.email],
                    senha = row[UsuariosTable.senha],
                    cpf = row[UsuariosTable.cpf],
                    telefone = row[UsuariosTable.telefone]
                )
            }
    }

    fun create(usuario: Usuario): Usuario = transaction {
        val hashedPassword = BCrypt.hashpw(usuario.senha, BCrypt.gensalt())
        val generatedId = UsuariosTable.insert {
            it[nome] = usuario.nome
            it[email] = usuario.email
            it[senha] = hashedPassword
            it[cpf] = usuario.cpf
            it[telefone] = usuario.telefone
            it[isAdmin] = usuario.isAdmin
        } get UsuariosTable.id

        usuario.copy(id = generatedId, senha = hashedPassword)
    }

    fun update(id: Int, usuario: Usuario): Boolean = transaction {
        UsuariosTable.update({ UsuariosTable.id eq id }) {
            it[nome] = usuario.nome
            it[email] = usuario.email
            it[senha] = BCrypt.hashpw(usuario.senha, BCrypt.gensalt())
            it[cpf] = usuario.cpf
            it[telefone] = usuario.telefone
            it[isAdmin] = usuario.isAdmin
        } > 0
    }

    fun delete(id: Int): Boolean = transaction {
        UsuariosTable.deleteWhere { UsuariosTable.id eq id } > 0
    }

    fun verifyLogin(email: String, password: String): Boolean {
        val usuario = findByEmail(email) ?: return false
        return BCrypt.checkpw(password, usuario.senha)
    }
}
