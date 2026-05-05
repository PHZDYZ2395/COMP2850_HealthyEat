/**
 * FoodEntry entity representing a user's food diary record.
 * Stores food name, portion size, calories, meal type, and entry date.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "food_entries")
data class FoodEntry(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "food_name", nullable = false)
    var foodName: String,

    @Column(name = "portion_size")
    var portionSize: String? = null,

    @Column(nullable = false)
    var calories: Int,

    @Column(name = "meal_type")
    var mealType: String? = null,

    @Column(name = "entry_date", nullable = false)
    var entryDate: LocalDate,

    @Column(columnDefinition = "TEXT")
    var notes: String? = null,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
