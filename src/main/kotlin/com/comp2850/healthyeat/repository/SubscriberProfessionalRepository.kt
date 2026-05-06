/**
 * SubscriberProfessionalRepository for managing subscriber-professional relationships.
 * Provides methods to find clients of a professional and professionals of a subscriber.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.SubscriberProfessional
import org.springframework.data.jpa.repository.JpaRepository

interface SubscriberProfessionalRepository : JpaRepository<SubscriberProfessional, Long> {
    fun findByProfessionalId(professionalId: Long): List<SubscriberProfessional>
    fun findBySubscriberId(subscriberId: Long): List<SubscriberProfessional>
    fun findByProfessionalIdAndSubscriberId(professionalId: Long, subscriberId: Long): SubscriberProfessional?
    fun existsByProfessionalIdAndSubscriberId(professionalId: Long, subscriberId: Long): Boolean
    fun countByProfessionalId(professionalId: Long): Long
}
