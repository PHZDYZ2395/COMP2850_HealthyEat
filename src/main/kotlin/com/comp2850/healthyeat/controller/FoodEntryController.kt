/**
 * FoodEntryController handles food diary CRUD operations and trend analysis for subscribers.
 * Provides endpoints for managing food entries and viewing calorie trends.
 * Depends on FoodEntryService and UserService for business logic.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.controller

import com.comp2850.healthyeat.dto.FoodEntryRequest
import com.comp2850.healthyeat.dto.FoodEntryResponse
import com.comp2850.healthyeat.dto.TrendResponse
import com.comp2850.healthyeat.service.FoodEntryService
import com.comp2850.healthyeat.service.UserService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/food-entries")
class FoodEntryController(
    private val foodEntryService: FoodEntryService,
    private val userService: UserService
) {

    /**
     * Get food entries for the current user, optionally filtered by date.
     * @param userDetails authenticated user
     * @param date optional date filter (YYYY-MM-DD)
     * @return list of food entries
     */
    @GetMapping
    fun getEntries(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?
    ): ResponseEntity<List<FoodEntryResponse>> {
        val user = userService.getUserByEmail(userDetails.username)
        val entries = foodEntryService.getEntriesByUser(user.id, date)
        return ResponseEntity.ok(entries)
    }

    /**
     * Add a new food entry.
     * @param userDetails authenticated user
     * @param request food entry data
     * @return created food entry
     */
    @PostMapping
    fun createEntry(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody request: FoodEntryRequest
    ): ResponseEntity<FoodEntryResponse> {
        val user = userService.getUserByEmail(userDetails.username)
        val entry = foodEntryService.createEntry(user.id, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(entry)
    }

    /**
     * Update an existing food entry.
     * @param id the entry ID
     * @param request updated food entry data
     * @return updated food entry
     */
    @PutMapping("/{id}")
    fun updateEntry(
        @PathVariable id: Long,
        @RequestBody request: FoodEntryRequest
    ): ResponseEntity<FoodEntryResponse> {
        val entry = foodEntryService.updateEntry(id, request)
        return ResponseEntity.ok(entry)
    }

    /**
     * Delete a food entry.
     * @param id the entry ID
     * @return no content
     */
    @DeleteMapping("/{id}")
    fun deleteEntry(@PathVariable id: Long): ResponseEntity<Void> {
        foodEntryService.deleteEntry(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Get calorie trends for the last 7 days.
     * @param userDetails authenticated user
     * @return list of trend data points
     */
    @GetMapping("/trends")
    fun getTrends(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<List<TrendResponse>> {
        val user = userService.getUserByEmail(userDetails.username)
        val trends = foodEntryService.getCalorieTrends(user.id)
        return ResponseEntity.ok(trends)
    }
}
