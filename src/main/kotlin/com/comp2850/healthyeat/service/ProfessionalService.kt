/**
 * ProfessionalService handles health professional business logic including client management and advice.
 * Depends on SubscriberProfessionalRepository, ProfessionalAdviceRepository, UserRepository, and UserService.
 * Used by ProfessionalController.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.service

import com.comp2850.healthyeat.domain.ProfessionalAdvice
import com.comp2850.healthyeat.domain.SubscriberProfessional
import com.comp2850.healthyeat.domain.UserRole
import com.comp2850.healthyeat.dto.AdviceRequest
import com.comp2850.healthyeat.dto.AdviceResponse
import com.comp2850.healthyeat.dto.UserInfoResponse
import com.comp2850.healthyeat.repository.ProfessionalAdviceRepository
import com.comp2850.healthyeat.repository.SubscriberProfessionalRepository
import com.comp2850.healthyeat.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProfessionalService(
    private val subscriberProfessionalRepository: SubscriberProfessionalRepository,
    private val professionalAdviceRepository: ProfessionalAdviceRepository,
    private val userService: UserService,
    private val userRepository: UserRepository
) {

    /**
     * Get all clients (subscribers) assigned to a professional.
     * @param professionalId the professional's user ID
     * @return list of UserInfoResponse for each client
     */
    @Transactional(readOnly = true)
    fun getClients(professionalId: Long): List<UserInfoResponse> {
        val relationships = subscriberProfessionalRepository.findByProfessionalId(professionalId)
        return relationships.mapNotNull { rel ->
            try {
                val user = userService.getUserById(rel.subscriberId)
                UserInfoResponse(
                    id = user.id!!,
                    email = user.email,
                    fullName = user.fullName,
                    role = user.role.name,
                    enabled = user.enabled
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Give advice to a subscriber.
     * @param professionalId the professional's user ID
     * @param subscriberId the subscriber's user ID
     * @param request advice text
     * @return AdviceResponse
     */
    @Transactional
    fun giveAdvice(professionalId: Long, subscriberId: Long, request: AdviceRequest): AdviceResponse {
        val advice = ProfessionalAdvice(
            professionalId = professionalId,
            subscriberId = subscriberId,
            adviceText = request.adviceText
        )
        val saved = professionalAdviceRepository.save(advice)
        val professional = userService.getUserById(professionalId)
        return AdviceResponse(
            id = saved.id!!,
            professionalId = saved.professionalId,
            professionalName = professional.fullName,
            subscriberId = saved.subscriberId,
            adviceText = saved.adviceText,
            createdAt = saved.createdAt
        )
    }

    /**
     * Get advice given by a professional to a specific subscriber.
     * @param professionalId the professional's user ID
     * @param subscriberId the subscriber's user ID
     * @return list of AdviceResponse
     */
    @Transactional(readOnly = true)
    fun getAdviceForClient(professionalId: Long, subscriberId: Long): List<AdviceResponse> {
        val professional = userService.getUserById(professionalId)
        return professionalAdviceRepository
            .findByProfessionalIdAndSubscriberIdOrderByCreatedAtDesc(professionalId, subscriberId)
            .map { AdviceResponse(
                id = it.id!!,
                professionalId = it.professionalId,
                professionalName = professional.fullName,
                subscriberId = it.subscriberId,
                adviceText = it.adviceText,
                createdAt = it.createdAt
            )}
    }

    /**
     * Get all advice given by a professional.
     * @param professionalId the professional's user ID
     * @return list of AdviceResponse
     */
    @Transactional(readOnly = true)
    fun getAdviceByProfessional(professionalId: Long): List<AdviceResponse> {
        val professional = userService.getUserById(professionalId)
        return professionalAdviceRepository
            .findByProfessionalIdOrderByCreatedAtDesc(professionalId)
            .map { AdviceResponse(
                id = it.id!!,
                professionalId = it.professionalId,
                professionalName = professional.fullName,
                subscriberId = it.subscriberId,
                adviceText = it.adviceText,
                createdAt = it.createdAt
            )}
    }

    /**
     * Assign a subscriber to a professional.
     * @param professionalId the professional's user ID
     * @param subscriberId the subscriber's user ID
     */
    @Transactional
    fun assignClient(professionalId: Long, subscriberId: Long) {
        if (!subscriberProfessionalRepository.existsByProfessionalIdAndSubscriberId(professionalId, subscriberId)) {
            subscriberProfessionalRepository.save(
                SubscriberProfessional(
                    professionalId = professionalId,
                    subscriberId = subscriberId
                )
            )
        }
    }

    /**
     * Get client count for a professional.
     * @param professionalId the professional's user ID
     * @return number of clients
     */
    @Transactional(readOnly = true)
    fun getClientCount(professionalId: Long): Long {
        return subscriberProfessionalRepository.countByProfessionalId(professionalId)
    }

    @Transactional(readOnly = true)
    fun getAvailableProfessionals(): List<UserInfoResponse> {
        return userRepository.findByRoleAndEnabled(UserRole.PROFESSIONAL, true)
            .map { UserInfoResponse(
                id = it.id!!,
                email = it.email,
                fullName = it.fullName,
                role = it.role.name,
                enabled = it.enabled
            )}
    }

    @Transactional(readOnly = true)
    fun getCurrentProfessional(subscriberId: Long): UserInfoResponse? {
        val rel = subscriberProfessionalRepository.findBySubscriberId(subscriberId).firstOrNull()
        return rel?.let {
            try {
                val user = userService.getUserById(it.professionalId)
                UserInfoResponse(
                    id = user.id!!,
                    email = user.email,
                    fullName = user.fullName,
                    role = user.role.name,
                    enabled = user.enabled
                )
            } catch (e: Exception) { null }
        }
    }

    @Transactional
    fun removeClient(professionalId: Long, subscriberId: Long) {
        val rel = subscriberProfessionalRepository.findByProfessionalIdAndSubscriberId(professionalId, subscriberId)
        if (rel != null) {
            subscriberProfessionalRepository.delete(rel)
        }
    }
}
