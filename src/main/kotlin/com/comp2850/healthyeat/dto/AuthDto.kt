/**
 * DTOs for authentication requests and responses.
 * Contains LoginRequest, RegisterRequest, AuthResponse, and UserInfoResponse data classes.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String
)

data class AuthResponse(
    val token: String,
    val user: UserInfoResponse
)

data class UserInfoResponse(
    val id: Long,
    val email: String,
    val fullName: String,
    val role: String,
    val enabled: Boolean
)

data class UpdateProfileRequest(
    val fullName: String,
    val email: String
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
