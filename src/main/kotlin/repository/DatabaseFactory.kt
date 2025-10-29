package com.eventos.repository

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    lateinit var db: Database

    fun init(application: Application) {
        val config = application.environment.config
        val url = config.property("database.url").getString()
        val user = config.property("database.user").getString()
        val password = config.property("database.password").getString()
        val driver = config.property("database.driver").getString()

        db = Database.connect(url, driver = driver, user = user, password = password)
    }
}
