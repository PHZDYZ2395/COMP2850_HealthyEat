/**
 * DTOs for admin-related requests and responses.
 * Contains AdminUserResponse, EnableUserRequest, RoleChangeRequest, ProfessionalStats, and SystemStats.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.dto

import com.comp2850.healthyeat.domain.UserRole

data class AdminUserResponse(
    val id: Long,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val enabled: Boolean,
    val createdAt: java.time.LocalDateTime
)

data class EnableUserRequest(
    val enabled: Boolean
)

data class RoleChangeRequest(
    val role: UserRole
)

data class ProfessionalStats(
    val professionalId: Long,
    val professionalName: String,
    val professionalEmail: String,
    val clientCount: Long
)

data class SystemStats(
    val totalUsers: Long,
    val totalSubscribers: Long,
    val totalProfessionals: Long,
    val totalFoodEntries: Long,
    val totalAdvice: Long
)
