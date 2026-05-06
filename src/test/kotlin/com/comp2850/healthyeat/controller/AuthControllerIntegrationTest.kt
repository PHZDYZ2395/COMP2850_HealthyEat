/**
 * Integration tests for AuthController.
 * Tests login endpoint with MockMvc.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.controller

import com.comp2850.healthyeat.domain.User
import com.comp2850.healthyeat.domain.UserRole
import com.comp2850.healthyeat.repository.UserRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    private val mapper = jacksonObjectMapper()

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
        userRepository.save(
            User(
                email = "test@example.com",
                passwordHash = passwordEncoder.encode("password123"),
                fullName = "Test User",
                role = UserRole.SUBSCRIBER,
                enabled = true
            )
        )
    }

    @Test
    fun `login returns token for valid credentials`() {
        val loginRequest = mapOf("email" to "test@example.com", "password" to "password123")

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").isNotEmpty)
            .andExpect(jsonPath("$.user.email").value("test@example.com"))
    }

    @Test
    fun `login returns 400 for invalid credentials`() {
        val loginRequest = mapOf("email" to "test@example.com", "password" to "wrongpassword")

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `register creates new user and returns token`() {
        val registerRequest = mapOf(
            "email" to "newuser@example.com",
            "password" to "newpassword",
            "fullName" to "New User"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(registerRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").isNotEmpty)
            .andExpect(jsonPath("$.user.email").value("newuser@example.com"))
    }
}
