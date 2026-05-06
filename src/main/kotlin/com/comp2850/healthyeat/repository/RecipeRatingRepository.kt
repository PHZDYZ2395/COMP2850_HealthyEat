/**
 * RecipeRatingRepository for accessing and managing RecipeRating entities.
 * Provides methods to calculate average rating and check if user has rated.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.RecipeRating
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RecipeRatingRepository : JpaRepository<RecipeRating, Long> {
    @Query("SELECT AVG(r.rating) FROM RecipeRating r WHERE r.recipeId = :recipeId")
    fun averageRating(@Param("recipeId") recipeId: Long): Double?

    @Query("SELECT COUNT(r) FROM RecipeRating r WHERE r.recipeId = :recipeId")
    fun countByRecipeId(@Param("recipeId") recipeId: Long): Long

    fun findByUserIdAndRecipeId(userId: Long, recipeId: Long): RecipeRating?
    fun deleteByUserIdAndRecipeId(userId: Long, recipeId: Long)
}
