/**
 * AdviceController handles advice-related endpoints for subscribers.
 * Allows subscribers to view advice given to them by professionals.
 * Depends on ProfessionalAdviceRepository and UserService.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.controller

import com.comp2850.healthyeat.dto.AdviceResponse
import com.comp2850.healthyeat.repository.ProfessionalAdviceRepository
import com.comp2850.healthyeat.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/advice")
class AdviceController(
    private val professionalAdviceRepository: ProfessionalAdviceRepository,
    private val userService: UserService
) {

    /**
     * Get all advice given to the current subscriber.
     * @param userDetails authenticated user
     * @return list of advice responses
     */
    @GetMapping
    fun getAdviceForSubscriber(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<List<AdviceResponse>> {
        val user = userService.getUserByEmail(userDetails.username)
        val adviceList = professionalAdviceRepository.findBySubscriberIdOrderByCreatedAtDesc(user.id)
        val responses = adviceList.map { advice ->
            val professional = userService.getUserById(advice.professionalId)
            AdviceResponse(
                id = advice.id!!,
                professionalId = advice.professionalId,
                professionalName = professional.fullName,
                subscriberId = advice.subscriberId,
                adviceText = advice.adviceText,
                createdAt = advice.createdAt
            )
        }
        return ResponseEntity.ok(responses)
    }
}
