/**
 * SubscriberController handles subscriber-specific endpoints.
 * Allows subscribers to manage their assigned nutritionist.
 * Depends on ProfessionalService and UserService.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.controller

import com.comp2850.healthyeat.dto.UserInfoResponse
import com.comp2850.healthyeat.service.ProfessionalService
import com.comp2850.healthyeat.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/subscribers")
class SubscriberController(
    private val professionalService: ProfessionalService,
    private val userService: UserService
) {

    @GetMapping("/professionals")
    fun getAvailableProfessionals(): ResponseEntity<List<UserInfoResponse>> {
        return ResponseEntity.ok(professionalService.getAvailableProfessionals())
    }

    @GetMapping("/my-professional")
    fun getMyProfessional(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<UserInfoResponse?> {
        val user = userService.getUserByEmail(userDetails.username)
        val professional = professionalService.getCurrentProfessional(user.id)
        return if (professional != null) ResponseEntity.ok(professional) else ResponseEntity.noContent().build()
    }

    @PostMapping("/my-professional")
    fun assignProfessional(
        @RequestBody request: AssignProfessionalRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Void> {
        val user = userService.getUserByEmail(userDetails.username)
        val current = professionalService.getCurrentProfessional(user.id)
        if (current != null) {
            professionalService.removeClient(current.id, user.id)
        }
        professionalService.assignClient(request.professionalId, user.id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    @DeleteMapping("/my-professional")
    fun removeProfessional(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Void> {
        val user = userService.getUserByEmail(userDetails.username)
        val current = professionalService.getCurrentProfessional(user.id)
        if (current != null) {
            professionalService.removeClient(current.id, user.id)
        }
        return ResponseEntity.noContent().build()
    }
}

data class AssignProfessionalRequest(val professionalId: Long)
