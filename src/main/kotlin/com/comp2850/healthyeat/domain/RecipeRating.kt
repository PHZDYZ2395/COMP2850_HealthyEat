/**
 * RecipeRating entity representing a user's rating for a recipe.
 * Stores user ID, recipe ID, rating value (1-5), and timestamp.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "recipe_ratings")
data class RecipeRating(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "recipe_id", nullable = false)
    val recipeId: Long,

    @Column(nullable = false)
    var rating: Int,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
