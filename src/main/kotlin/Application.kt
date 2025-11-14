package com.eventos

import com.eventos.plugins.GatewayValidationPlugin
import com.eventos.repository.DatabaseFactory
import com.eventos.routes.authRoutes
import com.eventos.routes.usuarioRoutes
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    install(GatewayValidationPlugin)

    DatabaseFactory.init(this)
    usuarioRoutes()
    authRoutes()
}