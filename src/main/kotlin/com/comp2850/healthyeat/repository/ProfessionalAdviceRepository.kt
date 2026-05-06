/**
 * ProfessionalAdviceRepository for accessing and managing ProfessionalAdvice entities.
 * Provides methods to find advice by subscriber or professional.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.ProfessionalAdvice
import org.springframework.data.jpa.repository.JpaRepository

interface ProfessionalAdviceRepository : JpaRepository<ProfessionalAdvice, Long> {
    fun findBySubscriberIdOrderByCreatedAtDesc(subscriberId: Long): List<ProfessionalAdvice>
    fun findByProfessionalIdOrderByCreatedAtDesc(professionalId: Long): List<ProfessionalAdvice>
    fun findByProfessionalIdAndSubscriberIdOrderByCreatedAtDesc(professionalId: Long, subscriberId: Long): List<ProfessionalAdvice>
}
