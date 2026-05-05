/**
 * FoodEntryService handles food diary business logic including CRUD operations and trend analysis.
 * Depends on FoodEntryRepository for data access.
 * Used by FoodEntryController and ProfessionalController.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.service

import com.comp2850.healthyeat.domain.FoodEntry
import com.comp2850.healthyeat.dto.FoodEntryRequest
import com.comp2850.healthyeat.dto.FoodEntryResponse
import com.comp2850.healthyeat.dto.TrendResponse
import com.comp2850.healthyeat.repository.FoodEntryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class FoodEntryService(
    private val foodEntryRepository: FoodEntryRepository
) {

    /**
     * Create a new food entry for a user.
     * @param userId the user ID
     * @param request food entry data
     * @return FoodEntryResponse
     */
    @Transactional
    fun createEntry(userId: Long, request: FoodEntryRequest): FoodEntryResponse {
        val entry = FoodEntry(
            userId = userId,
            foodName = request.foodName,
            portionSize = request.portionSize,
            calories = request.calories,
            mealType = request.mealType,
            entryDate = request.date,
            notes = request.notes
        )
        val saved = foodEntryRepository.save(entry)
        return toResponse(saved)
    }

    /**
     * Update an existing food entry.
     * @param entryId the entry ID
     * @param request updated food entry data
     * @return FoodEntryResponse
     * @throws RuntimeException if entry not found
     */
    @Transactional
    fun updateEntry(entryId: Long, request: FoodEntryRequest): FoodEntryResponse {
        val entry = foodEntryRepository.findById(entryId)
            .orElseThrow { RuntimeException("Food entry not found") }
        entry.foodName = request.foodName
        entry.portionSize = request.portionSize
        entry.calories = request.calories
        entry.mealType = request.mealType
        entry.entryDate = request.date
        entry.notes = request.notes
        val saved = foodEntryRepository.save(entry)
        return toResponse(saved)
    }

    /**
     * Delete a food entry by ID.
     * @param entryId the entry ID
     */
    @Transactional
    fun deleteEntry(entryId: Long) {
        foodEntryRepository.deleteById(entryId)
    }

    /**
     * Get all food entries for a user, optionally filtered by date.
     * @param userId the user ID
     * @param date optional date filter
     * @return list of FoodEntryResponse
     */
    @Transactional(readOnly = true)
    fun getEntriesByUser(userId: Long, date: LocalDate?): List<FoodEntryResponse> {
        val entries = if (date != null) {
            foodEntryRepository.findByUserIdAndEntryDate(userId, date)
        } else {
            foodEntryRepository.findByUserIdOrderByEntryDateDesc(userId)
        }
        return entries.map { toResponse(it) }
    }

    /**
     * Get calorie trends for the last 7 days for a user.
     * @param userId the user ID
     * @return list of TrendResponse with date and total calories
     */
    @Transactional(readOnly = true)
    fun getCalorieTrends(userId: Long): List<TrendResponse> {
        val startDate = LocalDate.now().minusDays(6)
        val results = foodEntryRepository.findCaloriesTrend(userId, startDate)
        return results.map { TrendResponse(
            date = it[0] as LocalDate,
            totalCalories = it[1] as Long
        )}
    }

    /**
     * Count total food entries in the system.
     * @return total count
     */
    @Transactional(readOnly = true)
    fun countAllEntries(): Long = foodEntryRepository.count()

    private fun toResponse(entry: FoodEntry) = FoodEntryResponse(
        id = entry.id!!,
        userId = entry.userId,
        foodName = entry.foodName,
        portionSize = entry.portionSize,
        calories = entry.calories,
        mealType = entry.mealType,
        entryDate = entry.entryDate,
        notes = entry.notes,
        createdAt = entry.createdAt
    )
}
