/**
 * Unit tests for UserService.
 * Tests registration, login, and user management logic.
 * Uses Mockito for mocking dependencies.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.service

import com.comp2850.healthyeat.domain.User
import com.comp2850.healthyeat.domain.UserRole
import com.comp2850.healthyeat.dto.LoginRequest
import com.comp2850.healthyeat.dto.RegisterRequest
import com.comp2850.healthyeat.repository.UserRepository
import com.comp2850.healthyeat.security.JwtUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var jwtUtil: JwtUtil
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        passwordEncoder = mock(PasswordEncoder::class.java)
        jwtUtil = mock(JwtUtil::class.java)
        userService = UserService(userRepository, passwordEncoder, jwtUtil)
    }

    @Test
    fun `registerSubscriber creates user and returns token`() {
        val request = RegisterRequest("test@example.com", "password123", "Test User")
        `when`(userRepository.existsByEmail("test@example.com")).thenReturn(false)
        `when`(passwordEncoder.encode("password123")).thenReturn("\$2a\$10\$encoded")
        `when`(userRepository.save(any())).thenAnswer { invocation ->
            val user = invocation.getArgument<User>(0)
            user.copy(id = 1L)
        }
        `when`(jwtUtil.generateToken("test@example.com", "SUBSCRIBER")).thenReturn("fake-token")

        val result = userService.registerSubscriber(request)

        assertNotNull(result)
        assertEquals("fake-token", result.token)
        assertEquals("test@example.com", result.user.email)
        assertEquals("SUBSCRIBER", result.user.role)
        verify(userRepository).save(any())
    }

    @Test
    fun `registerSubscriber throws when email exists`() {
        val request = RegisterRequest("test@example.com", "password123", "Test User")
        `when`(userRepository.existsByEmail("test@example.com")).thenReturn(true)

        val ex = assertThrows<RuntimeException> {
            userService.registerSubscriber(request)
        }
        assertEquals("Email already exists", ex.message)
    }

    @Test
    fun `login returns token for valid credentials`() {
        val request = LoginRequest("test@example.com", "password123")
        val user = User(
            id = 1L,
            email = "test@example.com",
            passwordHash = "\$2a\$10\$encoded",
            fullName = "Test User",
            role = UserRole.SUBSCRIBER,
            enabled = true
        )
        `when`(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user))
        `when`(passwordEncoder.matches("password123", "\$2a\$10\$encoded")).thenReturn(true)
        `when`(jwtUtil.generateToken("test@example.com", "SUBSCRIBER")).thenReturn("fake-token")

        val result = userService.login(request)

        assertNotNull(result)
        assertEquals("fake-token", result.token)
    }

    @Test
    fun `login throws for invalid password`() {
        val request = LoginRequest("test@example.com", "wrongpassword")
        val user = User(
            id = 1L,
            email = "test@example.com",
            passwordHash = "\$2a\$10\$encoded",
            fullName = "Test User",
            role = UserRole.SUBSCRIBER,
            enabled = true
        )
        `when`(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user))
        `when`(passwordEncoder.matches("wrongpassword", "\$2a\$10\$encoded")).thenReturn(false)

        val ex = assertThrows<RuntimeException> {
            userService.login(request)
        }
        assertEquals("Invalid email or password", ex.message)
    }

    @Test
    fun `login throws for disabled user`() {
        val request = LoginRequest("test@example.com", "password123")
        val user = User(
            id = 1L,
            email = "test@example.com",
            passwordHash = "\$2a\$10\$encoded",
            fullName = "Test User",
            role = UserRole.SUBSCRIBER,
            enabled = false
        )
        `when`(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user))
        `when`(passwordEncoder.matches("password123", "\$2a\$10\$encoded")).thenReturn(true)

        val ex = assertThrows<RuntimeException> {
            userService.login(request)
        }
        assertEquals("Account is disabled. Please contact an administrator.", ex.message)
    }
}
