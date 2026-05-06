/**
 * ProfessionalAdvice entity representing advice given by a health professional to a subscriber.
 * Stores advice text, professional ID, subscriber ID, and creation timestamp.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "professional_advice")
data class ProfessionalAdvice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "professional_id", nullable = false)
    val professionalId: Long,

    @Column(name = "subscriber_id", nullable = false)
    val subscriberId: Long,

    @Column(name = "advice_text", nullable = false, columnDefinition = "TEXT")
    val adviceText: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
