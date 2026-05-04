/**
 * UserService handles user-related business logic including registration, login, and user management.
 * Depends on UserRepository for data access and PasswordEncoder for password hashing.
 * Used by AuthController and AdminController.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.service

import com.comp2850.healthyeat.domain.User
import com.comp2850.healthyeat.domain.UserRole
import com.comp2850.healthyeat.dto.*
import com.comp2850.healthyeat.repository.UserRepository
import com.comp2850.healthyeat.security.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    /**
     * Register a new subscriber user.
     * @param request registration data (email, password, fullName)
     * @return AuthResponse with JWT token and user info
     * @throws RuntimeException if email already exists
     */
    @Transactional
    fun registerSubscriber(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw RuntimeException("Email already exists")
        }
        val user = User(
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            role = UserRole.SUBSCRIBER,
            enabled = true
        )
        val saved = userRepository.save(user)
        val token = jwtUtil.generateToken(saved.email, saved.role.name)
        return AuthResponse(token, toUserInfoResponse(saved))
    }

    /**
     * Register a new professional user (requires admin enablement).
     * @param request registration data (email, password, fullName)
     * @return AuthResponse with JWT token and user info
     * @throws RuntimeException if email already exists
     */
    @Transactional
    fun registerProfessional(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw RuntimeException("Email already exists")
        }
        val user = User(
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            role = UserRole.PROFESSIONAL,
            enabled = false
        )
        val saved = userRepository.save(user)
        val token = jwtUtil.generateToken(saved.email, saved.role.name)
        return AuthResponse(token, toUserInfoResponse(saved))
    }

    /**
     * Authenticate user and generate JWT token.
     * @param request login data (email, password)
     * @return AuthResponse with JWT token and user info
     * @throws RuntimeException if credentials are invalid
     */
    @Transactional(readOnly = true)
    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { RuntimeException("Invalid email or password") }
        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw RuntimeException("Invalid email or password")
        }
        if (!user.enabled) {
            throw RuntimeException("Account is disabled. Please contact an administrator.")
        }
        val token = jwtUtil.generateToken(user.email, user.role.name)
        return AuthResponse(token, toUserInfoResponse(user))
    }

    /**
     * Get user information by email.
     * @param email the user's email address
     * @return UserInfoResponse
     * @throws RuntimeException if user not found
     */
    @Transactional(readOnly = true)
    fun getUserByEmail(email: String): UserInfoResponse {
        val user = userRepository.findByEmail(email)
            .orElseThrow { RuntimeException("User not found") }
        return toUserInfoResponse(user)
    }

    /**
     * Get all users, optionally filtered by role.
     * @param role optional role filter
     * @return list of AdminUserResponse
     */
    @Transactional(readOnly = true)
    fun getAllUsers(role: UserRole?): List<AdminUserResponse> {
        val users = if (role != null) {
            userRepository.findByRole(role)
        } else {
            userRepository.findAll()
        }
        return users.map { toAdminUserResponse(it) }
    }

    /**
     * Enable or disable a user.
     * @param userId the user ID
     * @param enabled whether to enable or disable
     */
    @Transactional
    fun setUserEnabled(userId: Long, enabled: Boolean) {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        user.enabled = enabled
        userRepository.save(user)
    }

    /**
     * Change a user's role.
     * @param userId the user ID
     * @param role the new role
     */
    @Transactional
    fun setUserRole(userId: Long, role: UserRole) {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        user.role = role
        userRepository.save(user)
    }

    /**
     * Delete a user by ID.
     * @param userId the user ID
     */
    @Transactional
    fun deleteUser(userId: Long) {
        userRepository.deleteById(userId)
    }

    /**
     * Find user by ID.
     * @param userId the user ID
     * @return User entity
     * @throws RuntimeException if not found
     */
    @Transactional(readOnly = true)
    fun getUserById(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
    }

    private fun toUserInfoResponse(user: User) = UserInfoResponse(
        id = user.id!!,
        email = user.email,
        fullName = user.fullName,
        role = user.role.name,
        enabled = user.enabled
    )

    private fun toAdminUserResponse(user: User) = AdminUserResponse(
        id = user.id!!,
        email = user.email,
        fullName = user.fullName,
        role = user.role,
        enabled = user.enabled,
        createdAt = user.createdAt
    )

    /**
     * Update user profile (full name and email).
     * @param userId the user ID
     * @param request updated profile data
     * @return UserInfoResponse
     * @throws RuntimeException if email already used by another user
     */
    @Transactional
    fun updateProfile(userId: Long, request: UpdateProfileRequest): UserInfoResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        if (request.email != user.email && userRepository.existsByEmail(request.email)) {
            throw RuntimeException("Email already in use")
        }
        user.fullName = request.fullName
        user.email = request.email
        userRepository.save(user)
        return toUserInfoResponse(user)
    }

    /**
     * Change user password.
     * @param userId the user ID
     * @param request old and new passwords
     * @throws RuntimeException if old password is incorrect
     */
    @Transactional
    fun changePassword(userId: Long, request: ChangePasswordRequest) {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        if (!passwordEncoder.matches(request.oldPassword, user.passwordHash)) {
            throw RuntimeException("Old password is incorrect")
        }
        user.passwordHash = passwordEncoder.encode(request.newPassword)
        userRepository.save(user)
    }
}
