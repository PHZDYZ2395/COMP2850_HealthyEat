/**
 * FoodDatabase entity representing a food item with nutritional information.
 * Stores food name, category, calories per 100g, macronutrients, and image.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "food_database")
data class FoodDatabase(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var category: String,

    @Column(name = "calories_per_100g", nullable = false)
    var caloriesPer100g: Int,

    @Column(name = "protein")
    var protein: Double? = null,

    @Column(name = "carbs")
    var carbs: Double? = null,

    @Column(name = "fat")
    var fat: Double? = null,

    @Column(name = "fiber")
    var fiber: Double? = null,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
