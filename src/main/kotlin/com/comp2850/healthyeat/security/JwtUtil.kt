/**
 * JWT utility class for generating and validating JSON Web Tokens.
 * Handles token creation, parsing, and expiration checking.
 * Depends on io.jsonwebtoken (jjwt) library.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
) {

    // used GitHub Copilot to generate lines 26-28
    private fun getSigningKey(): SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    /**
     * Generate a JWT token for a given email and role.
     * @param email the user's email address
     * @param role the user's role (SUBSCRIBER/PROFESSIONAL/ADMIN)
     * @return JWT token string
     */
    fun generateToken(email: String, role: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)
        return Jwts.builder()
            .subject(email)
            .claim("role", "ROLE_$role")
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact()
    }

    /**
     * Extract email (subject) from a JWT token.
     * @param token the JWT token
     * @return email address stored in the token
     */
    fun getEmailFromToken(token: String): String =
        Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
            .subject

    /**
     * Extract role from a JWT token.
     * @param token the JWT token
     * @return role string (e.g., ROLE_SUBSCRIBER)
     */
    fun getRoleFromToken(token: String): String =
        Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
            .get("role", String::class.java)

    /**
     * Validate a JWT token by checking its signature and expiration.
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}
