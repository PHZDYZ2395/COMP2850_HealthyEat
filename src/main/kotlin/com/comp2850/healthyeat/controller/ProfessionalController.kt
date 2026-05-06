/**
 * ProfessionalController handles endpoints for health professionals.
 * Provides client management, client diary viewing, and advice giving functionality.
 * Depends on ProfessionalService, FoodEntryService, and UserService.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.controller

import com.comp2850.healthyeat.dto.AdviceRequest
import com.comp2850.healthyeat.dto.AdviceResponse
import com.comp2850.healthyeat.dto.FoodEntryResponse
import com.comp2850.healthyeat.dto.UserInfoResponse
import com.comp2850.healthyeat.service.FoodEntryService
import com.comp2850.healthyeat.service.ProfessionalService
import com.comp2850.healthyeat.service.UserService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/professional")
class ProfessionalController(
    private val professionalService: ProfessionalService,
    private val foodEntryService: FoodEntryService,
    private val userService: UserService
) {

    /**
     * Get all clients assigned to the current professional.
     * @param userDetails authenticated user
     * @return list of client user info
     */
    @GetMapping("/clients")
    fun getClients(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<List<UserInfoResponse>> {
        val user = userService.getUserByEmail(userDetails.username)
        val clients = professionalService.getClients(user.id)
        return ResponseEntity.ok(clients)
    }

    /**
     * View a client's food diary entries.
     * @param clientId the client's user ID
     * @param date optional date filter
     * @param userDetails authenticated user
     * @return list of food entries
     */
    @GetMapping("/clients/{clientId}/food-entries")
    fun getClientFoodEntries(
        @PathVariable clientId: Long,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<List<FoodEntryResponse>> {
        val entries = foodEntryService.getEntriesByUser(clientId, date)
        return ResponseEntity.ok(entries)
    }

    /**
     * Give advice to a client.
     * @param clientId the client's user ID
     * @param request advice text
     * @param userDetails authenticated user
     * @return created advice
     */
    @PostMapping("/clients/{clientId}/advice")
    fun giveAdvice(
        @PathVariable clientId: Long,
        @RequestBody request: AdviceRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<AdviceResponse> {
        val user = userService.getUserByEmail(userDetails.username)
        val advice = professionalService.giveAdvice(user.id, clientId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(advice)
    }

    /**
     * Get advice given to a specific client.
     * @param clientId the client's user ID
     * @param userDetails authenticated user
     * @return list of advice
     */
    @GetMapping("/clients/{clientId}/advice")
    fun getAdviceForClient(
        @PathVariable clientId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<List<AdviceResponse>> {
        val user = userService.getUserByEmail(userDetails.username)
        val advice = professionalService.getAdviceForClient(user.id, clientId)
        return ResponseEntity.ok(advice)
    }

    /**
     * Get all advice given by the current professional.
     * @param userDetails authenticated user
     * @return list of advice
     */
    @GetMapping("/advice")
    fun getMyAdvice(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<List<AdviceResponse>> {
        val user = userService.getUserByEmail(userDetails.username)
        val advice = professionalService.getAdviceByProfessional(user.id)
        return ResponseEntity.ok(advice)
    }
}
