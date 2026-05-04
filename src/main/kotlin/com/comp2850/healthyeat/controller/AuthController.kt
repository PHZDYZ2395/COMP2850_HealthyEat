/**
 * AuthController handles authentication endpoints including login and registration.
 * Provides public endpoints for user authentication and registration.
 * Depends on UserService for business logic.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.controller

import com.comp2850.healthyeat.dto.*
import com.comp2850.healthyeat.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class AuthController(
    private val userService: UserService
) {

    /**
     * User login endpoint.
     * @param request login credentials (email and password)
     * @return JWT token and user info
     */
    @PostMapping("/auth/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val response = userService.login(request)
        return ResponseEntity.ok(response)
    }

    /**
     * Register a new subscriber (regular user).
     * @param request registration data
     * @return JWT token and user info
     */
    @PostMapping("/auth/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = userService.registerSubscriber(request)
        return ResponseEntity.ok(response)
    }

    /**
     * Register a new professional (requires admin enablement).
     * @param request registration data
     * @return JWT token and user info
     */
    @PostMapping("/auth/register/professional")
    fun registerProfessional(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        val response = userService.registerProfessional(request)
        return ResponseEntity.ok(response)
    }

    /**
     * Get current authenticated user information.
     * @param userDetails the authenticated user from Spring Security
     * @return user info
     */
    @GetMapping("/users/me")
    fun getCurrentUser(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<UserInfoResponse> {
        val user = userService.getUserByEmail(userDetails.username)
        return ResponseEntity.ok(user)
    }

    /**
     * Update current authenticated user's profile.
     * @param userDetails the authenticated user
     * @param request updated profile data
     * @return updated user info
     */
    @PutMapping("/users/me")
    fun updateProfile(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody request: UpdateProfileRequest
    ): ResponseEntity<UserInfoResponse> {
        val user = userService.getUserByEmail(userDetails.username)
        val updated = userService.updateProfile(user.id, request)
        return ResponseEntity.ok(updated)
    }

    /**
     * Change current authenticated user's password.
     * @param userDetails the authenticated user
     * @param request old and new passwords
     * @return success message
     */
    @PutMapping("/users/me/password")
    fun changePassword(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody request: ChangePasswordRequest
    ): ResponseEntity<Map<String, String>> {
        val user = userService.getUserByEmail(userDetails.username)
        userService.changePassword(user.id, request)
        return ResponseEntity.ok(mapOf("message" to "Password changed successfully"))
    }
}
