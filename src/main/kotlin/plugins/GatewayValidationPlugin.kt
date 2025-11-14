package com.eventos.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.http.*
import io.ktor.server.request.httpMethod
import io.ktor.server.request.uri

val GatewayValidationPlugin = createApplicationPlugin(name = "GatewayValidationPlugin") {

    onCall { call ->
        val method = call.request.httpMethod.value
        val uri = call.request.uri

        this@createApplicationPlugin.application.log.info("Request: $method $uri")

        call.request.headers.forEach { key, values ->
            this@createApplicationPlugin.application.log.info("$key = $values")
        }

        val gatewayKey = call.request.headers["X-Gateway-Key"]
        val expectedKey = System.getenv("GATEWAY_KEY")

        if (gatewayKey == null || gatewayKey != expectedKey) {
            call.respond(
                HttpStatusCode.Forbidden,
                mapOf("error" to "Acesso negado")
            )
            // Interrompe corretamente o pipeline
            return@onCall
        }
    }

    onCallRespond { call, _ ->
        val status = call.response.status() ?: HttpStatusCode.InternalServerError
        this@createApplicationPlugin.application.log.info("Response Status Code: $status")
    }
}
