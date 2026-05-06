/**
 * AdminService handles admin-related business logic including user management and system statistics.
 * Depends on UserService, ProfessionalService, FoodEntryService, ProfessionalAdviceRepository, and UserRepository.
 * Used by AdminController.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.service

import com.comp2850.healthyeat.domain.UserRole
import com.comp2850.healthyeat.dto.ProfessionalStats
import com.comp2850.healthyeat.dto.SystemStats
import com.comp2850.healthyeat.repository.ProfessionalAdviceRepository
import com.comp2850.healthyeat.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminService(
    private val userService: UserService,
    private val professionalService: ProfessionalService,
    private val foodEntryService: FoodEntryService,
    private val professionalAdviceRepository: ProfessionalAdviceRepository,
    private val userRepository: UserRepository
) {

    /**
     * Get all professionals with their client counts.
     * @return list of ProfessionalStats
     */
    @Transactional(readOnly = true)
    fun getProfessionalsWithStats(): List<ProfessionalStats> {
        val professionals = userRepository.findByRole(UserRole.PROFESSIONAL)
        return professionals.map { prof ->
            ProfessionalStats(
                professionalId = prof.id!!,
                professionalName = prof.fullName,
                professionalEmail = prof.email,
                clientCount = professionalService.getClientCount(prof.id)
            )
        }
    }

    /**
     * Get system-wide statistics.
     * @return SystemStats with counts for users, entries, and advice
     */
    @Transactional(readOnly = true)
    fun getSystemStats(): SystemStats {
        val totalUsers = userRepository.count()
        val totalSubscribers = userRepository.countByRole(UserRole.SUBSCRIBER)
        val totalProfessionals = userRepository.countByRole(UserRole.PROFESSIONAL)
        val totalFoodEntries = foodEntryService.countAllEntries()
        val totalAdvice = professionalAdviceRepository.count()
        return SystemStats(
            totalUsers = totalUsers,
            totalSubscribers = totalSubscribers,
            totalProfessionals = totalProfessionals,
            totalFoodEntries = totalFoodEntries,
            totalAdvice = totalAdvice
        )
    }
}
