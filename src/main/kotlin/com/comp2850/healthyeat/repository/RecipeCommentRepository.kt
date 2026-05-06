/**
 * RecipeCommentRepository for accessing and managing RecipeComment entities.
 * Provides methods to find comments by recipe ID and delete by user/recipe.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.RecipeComment
import org.springframework.data.jpa.repository.JpaRepository

interface RecipeCommentRepository : JpaRepository<RecipeComment, Long> {
    fun findByRecipeIdOrderByCreatedAtDesc(recipeId: Long): List<RecipeComment>
    fun findByUserId(userId: Long): List<RecipeComment>
    fun findByIdAndUserId(id: Long, userId: Long): RecipeComment?
}
