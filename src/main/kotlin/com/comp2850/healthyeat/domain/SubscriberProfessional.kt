/**
 * SubscriberProfessional entity representing the relationship between a subscriber and a professional.
 * Used to track which professional is responsible for which subscriber.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.domain

import jakarta.persistence.*

@Entity
@Table(name = "subscriber_professional")
data class SubscriberProfessional(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "subscriber_id", nullable = false)
    val subscriberId: Long,

    @Column(name = "professional_id", nullable = false)
    val professionalId: Long
)
