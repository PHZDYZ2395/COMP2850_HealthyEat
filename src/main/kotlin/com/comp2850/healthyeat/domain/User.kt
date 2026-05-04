/**
 * User entity representing a system user with authentication and role information.
 * Stores user credentials, role (SUBSCRIBER/PROFESSIONAL/ADMIN), and enabled status.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @Column(name = "full_name", nullable = false)
    var fullName: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole,

    @Column(nullable = false)
    var enabled: Boolean = true,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class UserRole {
    SUBSCRIBER, PROFESSIONAL, ADMIN
}
