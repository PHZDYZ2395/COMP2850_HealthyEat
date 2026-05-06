/**
 * RecipeService handles recipe business logic including CRUD, ratings, comments, and pagination.
 * Depends on RecipeRepository, RecipeRatingRepository, RecipeCommentRepository, and UserService.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.service

import com.comp2850.healthyeat.domain.Recipe
import com.comp2850.healthyeat.dto.*
import com.comp2850.healthyeat.repository.RecipeCommentRepository
import com.comp2850.healthyeat.repository.RecipeRatingRepository
import com.comp2850.healthyeat.repository.RecipeRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val recipeRatingRepository: RecipeRatingRepository,
    private val recipeCommentRepository: RecipeCommentRepository,
    private val userService: UserService
) {

    /**
     * Get paginated recipes with search and filter.
     * @param keyword search keyword
     * @param category filter category
     * @param page page number
     * @param size page size
     * @return PageResponse of RecipeResponse
     */
    @Transactional(readOnly = true)
    fun getRecipes(keyword: String?, category: String?, page: Int, size: Int): PageResponse<RecipeResponse> {
        val pageable = PageRequest.of(page, size)
        val cleanKeyword = if (keyword.isNullOrBlank()) null else keyword
        val cleanCategory = if (category.isNullOrBlank()) null else category
        val pageResult = recipeRepository.searchAndFilter(cleanKeyword, cleanCategory, pageable)
        return PageResponse(
            content = pageResult.content.map { toResponse(it) },
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
            currentPage = pageResult.number,
            pageSize = pageResult.size
        )
    }

    /**
     * Get a single recipe by ID.
     * @param id recipe ID
     * @return RecipeResponse
     */
    @Transactional(readOnly = true)
    fun getRecipe(id: Long): RecipeResponse {
        val recipe = recipeRepository.findById(id)
            .orElseThrow { RuntimeException("Recipe not found") }
        return toResponse(recipe)
    }

    /**
     * Create a new recipe.
     * @param request recipe data
     * @param userId creator user ID
     * @return RecipeResponse
     */
    @Transactional
    fun createRecipe(request: RecipeRequest, userId: Long): RecipeResponse {
        val recipe = Recipe(
            title = request.title,
            description = request.description,
            ingredients = request.ingredients,
            instructions = request.instructions,
            imageUrl = request.imageUrl,
            category = request.category,
            difficulty = request.difficulty,
            prepTime = request.prepTime,
            servings = request.servings,
            calories = request.calories,
            createdBy = userId
        )
        val saved = recipeRepository.save(recipe)
        return toResponse(saved)
    }

    /**
     * Update an existing recipe.
     * @param id recipe ID
     * @param request updated recipe data
     * @return RecipeResponse
     */
    @Transactional
    fun updateRecipe(id: Long, request: RecipeRequest): RecipeResponse {
        val recipe = recipeRepository.findById(id)
            .orElseThrow { RuntimeException("Recipe not found") }
        recipe.title = request.title
        recipe.description = request.description
        recipe.ingredients = request.ingredients
        recipe.instructions = request.instructions
        recipe.imageUrl = request.imageUrl
        recipe.category = request.category
        recipe.difficulty = request.difficulty
        recipe.prepTime = request.prepTime
        recipe.servings = request.servings
        recipe.calories = request.calories
        val saved = recipeRepository.save(recipe)
        return toResponse(saved)
    }

    /**
     * Delete a recipe.
     * @param id recipe ID
     */
    @Transactional
    fun deleteRecipe(id: Long) {
        recipeRepository.deleteById(id)
    }

    /**
     * Rate a recipe.
     * @param userId user ID
     * @param recipeId recipe ID
     * @param request rating value
     */
    @Transactional
    fun rateRecipe(userId: Long, recipeId: Long, request: RatingRequest) {
        val existing = recipeRatingRepository.findByUserIdAndRecipeId(userId, recipeId)
        if (existing != null) {
            existing.rating = request.rating
            recipeRatingRepository.save(existing)
        } else {
            recipeRatingRepository.save(
                com.comp2850.healthyeat.domain.RecipeRating(
                    userId = userId,
                    recipeId = recipeId,
                    rating = request.rating
                )
            )
        }
    }

    /**
     * Get rating info for a recipe.
     * @param recipeId recipe ID
     * @param userId optional user ID for user's own rating
     * @return RatingResponse
     */
    @Transactional(readOnly = true)
    fun getRecipeRating(recipeId: Long, userId: Long?): RatingResponse {
        val avg = recipeRatingRepository.averageRating(recipeId) ?: 0.0
        val count = recipeRatingRepository.countByRecipeId(recipeId)
        val userRating = if (userId != null) {
            recipeRatingRepository.findByUserIdAndRecipeId(userId, recipeId)?.rating
        } else null
        return RatingResponse(averageRating = avg, ratingCount = count, userRating = userRating)
    }

    /**
     * Add a comment to a recipe.
     * @param userId user ID
     * @param recipeId recipe ID
     * @param request comment text
     * @return CommentResponse
     */
    @Transactional
    fun addRecipeComment(userId: Long, recipeId: Long, request: CommentRequest): CommentResponse {
        val comment = com.comp2850.healthyeat.domain.RecipeComment(
            userId = userId,
            recipeId = recipeId,
            commentText = request.commentText
        )
        val saved = recipeCommentRepository.save(comment)
        val user = userService.getUserById(userId)
        return CommentResponse(
            id = saved.id!!,
            userId = saved.userId,
            userName = user.fullName,
            commentText = saved.commentText,
            createdAt = saved.createdAt
        )
    }

    /**
     * Get comments for a recipe.
     * @param recipeId recipe ID
     * @return list of CommentResponse
     */
    @Transactional(readOnly = true)
    fun getRecipeComments(recipeId: Long): List<CommentResponse> {
        return recipeCommentRepository.findByRecipeIdOrderByCreatedAtDesc(recipeId).map { c ->
            val user = userService.getUserById(c.userId)
            CommentResponse(
                id = c.id!!,
                userId = c.userId,
                userName = user.fullName,
                commentText = c.commentText,
                createdAt = c.createdAt
            )
        }
    }

    /**
     * Delete a recipe comment.
     * @param commentId comment ID
     * @param userId user ID (for ownership check)
     */
    @Transactional
    fun deleteRecipeComment(commentId: Long, userId: Long) {
        val comment = recipeCommentRepository.findByIdAndUserId(commentId, userId)
            ?: throw RuntimeException("Comment not found or not yours")
        recipeCommentRepository.delete(comment)
    }

    private fun toResponse(recipe: Recipe): RecipeResponse {
        val creator = userService.getUserById(recipe.createdBy)
        val avgRating = recipeRatingRepository.averageRating(recipe.id!!)
        val ratingCount = recipeRatingRepository.countByRecipeId(recipe.id!!)
        return RecipeResponse(
            id = recipe.id!!,
            title = recipe.title,
            description = recipe.description,
            ingredients = recipe.ingredients,
            instructions = recipe.instructions,
            imageUrl = recipe.imageUrl,
            category = recipe.category,
            difficulty = recipe.difficulty,
            prepTime = recipe.prepTime,
            servings = recipe.servings,
            calories = recipe.calories,
            createdBy = recipe.createdBy,
            createdByName = creator.fullName,
            createdAt = recipe.createdAt,
            averageRating = avgRating,
            ratingCount = ratingCount
        )
    }
}
