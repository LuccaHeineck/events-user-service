package com.eventos.repository

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    lateinit var db: Database

    fun init(application: Application) {
        val config = application.environment.config
        val url = config.property("ktor.database.url").getString()
        val user = config.property("ktor.database.user").getString()
        val password = config.property("ktor.database.password").getString()
        val driver = config.property("ktor.database.driver").getString()

        db = Database.connect(url, driver = driver, user = user, password = password)
    }
}
