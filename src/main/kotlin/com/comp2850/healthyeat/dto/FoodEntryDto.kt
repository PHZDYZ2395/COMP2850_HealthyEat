/**
 * DTOs for food entry requests and responses.
 * Contains FoodEntryRequest, FoodEntryResponse, and TrendResponse data classes.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.dto

import java.time.LocalDate

data class FoodEntryRequest(
    val foodName: String,
    val portionSize: String?,
    val calories: Int,
    val mealType: String?,
    val date: LocalDate,
    val notes: String? = null
)

data class FoodEntryResponse(
    val id: Long,
    val userId: Long,
    val foodName: String,
    val portionSize: String?,
    val calories: Int,
    val mealType: String?,
    val entryDate: LocalDate,
    val notes: String?,
    val createdAt: java.time.LocalDateTime
)

data class TrendResponse(
    val date: LocalDate,
    val totalCalories: Long
)
