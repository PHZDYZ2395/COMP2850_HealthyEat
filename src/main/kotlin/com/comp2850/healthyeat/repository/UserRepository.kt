/**
 * UserRepository for accessing and managing User entities.
 * Provides methods to find users by email, role, and enabled status.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.User
import com.comp2850.healthyeat.domain.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User>
    fun existsByEmail(email: String): Boolean
    fun findByRole(role: UserRole): List<User>
    fun findByEnabled(enabled: Boolean): List<User>
    fun findByRoleAndEnabled(role: UserRole, enabled: Boolean): List<User>
    fun countByRole(role: UserRole): Long
}
