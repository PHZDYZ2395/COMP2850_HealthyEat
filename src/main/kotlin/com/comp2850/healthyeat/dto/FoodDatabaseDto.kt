/**
 * DTOs for food database requests and responses.
 * Contains FoodDatabaseRequest and FoodDatabaseResponse.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.dto

import java.time.LocalDateTime

data class FoodDatabaseRequest(
    val name: String,
    val category: String,
    val caloriesPer100g: Int,
    val protein: Double?,
    val carbs: Double?,
    val fat: Double?,
    val fiber: Double?,
    val imageUrl: String?
)

data class FoodDatabaseResponse(
    val id: Long,
    val name: String,
    val category: String,
    val caloriesPer100g: Int,
    val protein: Double?,
    val carbs: Double?,
    val fat: Double?,
    val fiber: Double?,
    val imageUrl: String?,
    val createdAt: LocalDateTime
)

data class FoodSuggestion(
    val name: String,
    val caloriesPer100g: Int
)
