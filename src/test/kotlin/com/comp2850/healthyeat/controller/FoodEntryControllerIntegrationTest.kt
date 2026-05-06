/**
 * Integration tests for FoodEntryController.
 * Tests authenticated food entry endpoints with MockMvc.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.controller

import com.comp2850.healthyeat.domain.User
import com.comp2850.healthyeat.domain.UserRole
import com.comp2850.healthyeat.repository.UserRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FoodEntryControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    private val mapper = jacksonObjectMapper()
    private lateinit var token: String
    private var userId: Long? = null

    @BeforeEach
    fun setUp() {
        userRepository.deleteAll()
        val user = userRepository.save(
            User(
                email = "subscriber@example.com",
                passwordHash = passwordEncoder.encode("password123"),
                fullName = "Subscriber User",
                role = UserRole.SUBSCRIBER,
                enabled = true
            )
        )
        userId = user.id!!

        // Login to get token
        val loginRequest = mapper.writeValueAsString(mapOf("email" to "subscriber@example.com", "password" to "password123"))
        val loginResult = mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest)
        ).andReturn()
        val loginResponse = mapper.readTree(loginResult.response.contentAsString)
        token = loginResponse.get("token").asText()
    }

    @Test
    fun `create food entry returns 201`() {
        val entry = mapOf(
            "foodName" to "Apple",
            "portionSize" to "1 medium",
            "calories" to 95,
            "mealType" to "Snack",
            "date" to LocalDate.now().toString()
        )

        mockMvc.perform(
            post("/api/food-entries")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(entry))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.foodName").value("Apple"))
    }

    @Test
    fun `get food entries returns list`() {
        mockMvc.perform(
            get("/api/food-entries")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `unauthenticated request returns 401`() {
        mockMvc.perform(
            get("/api/food-entries")
        )
            .andExpect { result -> assertTrue(result.response.status in listOf(401, 403)) }
    }
}
