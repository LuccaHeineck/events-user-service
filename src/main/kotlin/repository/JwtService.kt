package com.eventos.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.util.*

object JwtService {
    private const val secret = "mysecret"
    private const val issuer = "user-service"
    private const val validityInMs = 36_000_00 * 24 // 24h

    fun generateToken(email: String): String {
        val algorithm = Algorithm.HMAC256(secret)
        return JWT.create()
            .withIssuer(issuer)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
            .sign(algorithm)
    }

    fun verifyToken(token: String): Boolean {
        val algorithm = Algorithm.HMAC256(secret)
        return try {
            JWT.require(algorithm).withIssuer(issuer).build().verify(token)
            true
        } catch (e: JWTVerificationException) {
            false
        }
    }
}
