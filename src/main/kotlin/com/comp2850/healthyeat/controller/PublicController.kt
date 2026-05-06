/**
 * PublicController provides public (unauthenticated) query endpoints for recipes, knowledge articles, and food database.
 * Also provides rating and comment endpoints that require authentication.
 * Depends on RecipeService, KnowledgeService, and FoodDatabaseService.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.controller

import com.comp2850.healthyeat.dto.*
import com.comp2850.healthyeat.service.FoodDatabaseService
import com.comp2850.healthyeat.service.KnowledgeService
import com.comp2850.healthyeat.service.RecipeService
import com.comp2850.healthyeat.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class PublicController(
    private val recipeService: RecipeService,
    private val knowledgeService: KnowledgeService,
    private val foodDatabaseService: FoodDatabaseService,
    private val userService: UserService
) {

    // ==================== Recipes (Public) ====================

    /**
     * Get paginated recipes with optional search and filter.
     * @param keyword search keyword
     * @param category filter category
     * @param page page number
     * @param size page size
     * @return PageResponse of recipes
     */
    @GetMapping("/recipes")
    fun getRecipes(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "12") size: Int
    ): ResponseEntity<PageResponse<RecipeResponse>> {
        return ResponseEntity.ok(recipeService.getRecipes(keyword, category, page, size))
    }

    /**
     * Get a single recipe by ID.
     * @param id recipe ID
     * @return RecipeResponse
     */
    @GetMapping("/recipes/{id}")
    fun getRecipe(@PathVariable id: Long): ResponseEntity<RecipeResponse> {
        return ResponseEntity.ok(recipeService.getRecipe(id))
    }

    /**
     * Get rating info for a recipe.
     * @param id recipe ID
     * @param userDetails optional authenticated user
     * @return RatingResponse
     */
    @GetMapping("/recipes/{id}/rating")
    fun getRecipeRating(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<RatingResponse> {
        val userId = userDetails?.let { userService.getUserByEmail(it.username).id }
        return ResponseEntity.ok(recipeService.getRecipeRating(id, userId))
    }

    /**
     * Rate a recipe.
     * @param id recipe ID
     * @param request rating value
     * @param userDetails authenticated user
     * @return no content
     */
    @PostMapping("/recipes/{id}/rate")
    fun rateRecipe(
        @PathVariable id: Long,
        @RequestBody request: RatingRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Void> {
        val user = userService.getUserByEmail(userDetails.username)
        recipeService.rateRecipe(user.id, id, request)
        return ResponseEntity.noContent().build()
    }

    /**
     * Get comments for a recipe.
     * @param id recipe ID
     * @return list of comments
     */
    @GetMapping("/recipes/{id}/comments")
    fun getRecipeComments(@PathVariable id: Long): ResponseEntity<List<CommentResponse>> {
        return ResponseEntity.ok(recipeService.getRecipeComments(id))
    }

    /**
     * Add a comment to a recipe.
     * @param id recipe ID
     * @param request comment text
     * @param userDetails authenticated user
     * @return CommentResponse
     */
    @PostMapping("/recipes/{id}/comments")
    fun addRecipeComment(
        @PathVariable id: Long,
        @RequestBody request: CommentRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<CommentResponse> {
        val user = userService.getUserByEmail(userDetails.username)
        val comment = recipeService.addRecipeComment(user.id, id, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(comment)
    }

    /**
     * Delete a recipe comment.
     * @param id recipe ID
     * @param commentId comment ID
     * @param userDetails authenticated user
     * @return no content
     */
    @DeleteMapping("/recipes/{id}/comments/{commentId}")
    fun deleteRecipeComment(
        @PathVariable id: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Void> {
        val user = userService.getUserByEmail(userDetails.username)
        recipeService.deleteRecipeComment(commentId, user.id)
        return ResponseEntity.noContent().build()
    }

    // ==================== Knowledge (Public) ====================

    /**
     * Get paginated published knowledge articles with optional search and filter.
     * @param keyword search keyword
     * @param category filter category
     * @param page page number
     * @param size page size
     * @return PageResponse of articles
     */
    @GetMapping("/knowledge")
    fun getKnowledge(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "12") size: Int
    ): ResponseEntity<PageResponse<KnowledgeResponse>> {
        return ResponseEntity.ok(knowledgeService.getKnowledge(keyword, category, page, size))
    }

    /**
     * Get a single knowledge article by ID.
     * @param id article ID
     * @return KnowledgeDetailResponse
     */
    @GetMapping("/knowledge/{id}")
    fun getKnowledge(@PathVariable id: Long): ResponseEntity<KnowledgeDetailResponse> {
        return ResponseEntity.ok(knowledgeService.getKnowledgeDetail(id))
    }

    /**
     * Get latest published articles for homepage.
     * @return list of articles
     */
    @GetMapping("/knowledge/latest")
    fun getLatestKnowledge(): ResponseEntity<List<KnowledgeResponse>> {
        return ResponseEntity.ok(knowledgeService.getLatestKnowledge(5))
    }

    /**
     * Get rating info for a knowledge article.
     * @param id article ID
     * @param userDetails optional authenticated user
     * @return RatingResponse
     */
    @GetMapping("/knowledge/{id}/rating")
    fun getArticleRating(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<RatingResponse> {
        val userId = userDetails?.let { userService.getUserByEmail(it.username).id }
        return ResponseEntity.ok(knowledgeService.getArticleRating(id, userId))
    }

    /**
     * Rate a knowledge article.
     * @param id article ID
     * @param request rating value
     * @param userDetails authenticated user
     * @return no content
     */
    @PostMapping("/knowledge/{id}/rate")
    fun rateArticle(
        @PathVariable id: Long,
        @RequestBody request: RatingRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Void> {
        val user = userService.getUserByEmail(userDetails.username)
        knowledgeService.rateArticle(user.id, id, request)
        return ResponseEntity.noContent().build()
    }

    /**
     * Get comments for a knowledge article.
     * @param id article ID
     * @return list of comments
     */
    @GetMapping("/knowledge/{id}/comments")
    fun getArticleComments(@PathVariable id: Long): ResponseEntity<List<CommentResponse>> {
        return ResponseEntity.ok(knowledgeService.getArticleComments(id))
    }

    /**
     * Add a comment to a knowledge article.
     * @param id article ID
     * @param request comment text
     * @param userDetails authenticated user
     * @return CommentResponse
     */
    @PostMapping("/knowledge/{id}/comments")
    fun addArticleComment(
        @PathVariable id: Long,
        @RequestBody request: CommentRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<CommentResponse> {
        val user = userService.getUserByEmail(userDetails.username)
        val comment = knowledgeService.addArticleComment(user.id, id, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(comment)
    }

    /**
     * Delete a knowledge article comment.
     * @param id article ID
     * @param commentId comment ID
     * @param userDetails authenticated user
     * @return no content
     */
    @DeleteMapping("/knowledge/{id}/comments/{commentId}")
    fun deleteArticleComment(
        @PathVariable id: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Void> {
        val user = userService.getUserByEmail(userDetails.username)
        knowledgeService.deleteArticleComment(commentId, user.id)
        return ResponseEntity.noContent().build()
    }

    // ==================== Food Database (Public) ====================

    /**
     * Get paginated food database entries with optional search and filter.
     * @param keyword search keyword
     * @param category filter category
     * @param page page number
     * @param size page size
     * @return PageResponse of food entries
     */
    @GetMapping("/food-database")
    fun getFoodDatabase(
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "12") size: Int
    ): ResponseEntity<PageResponse<FoodDatabaseResponse>> {
        return ResponseEntity.ok(foodDatabaseService.getFoodDatabase(keyword, category, page, size))
    }

    /**
     * Get a single food database entry by ID.
     * @param id entry ID
     * @return FoodDatabaseResponse
     */
    @GetMapping("/food-database/{id}")
    fun getFoodEntry(@PathVariable id: Long): ResponseEntity<FoodDatabaseResponse> {
        return ResponseEntity.ok(foodDatabaseService.getFoodEntry(id))
    }

    /**
     * Get popular food entries for homepage.
     * @return list of popular foods
     */
    @GetMapping("/food-database/popular")
    fun getPopularFoods(): ResponseEntity<List<FoodDatabaseResponse>> {
        return ResponseEntity.ok(foodDatabaseService.getPopularFoods(8))
    }

    /**
     * Search food entries by keyword for autocomplete suggestions.
     * @param keyword search keyword
     * @return list of food suggestions with name and calories
     */
    @GetMapping("/food-database/search")
    fun searchFoods(
        @RequestParam(required = false) keyword: String?
    ): ResponseEntity<List<com.comp2850.healthyeat.dto.FoodSuggestion>> {
        return ResponseEntity.ok(foodDatabaseService.searchFoods(keyword))
    }
}
