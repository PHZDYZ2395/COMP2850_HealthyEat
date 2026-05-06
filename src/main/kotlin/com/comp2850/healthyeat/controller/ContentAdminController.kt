/**
 * ContentAdminController provides admin CRUD endpoints for recipes, knowledge articles, and food database.
 * Also handles file upload for images.
 * Depends on RecipeService, KnowledgeService, FoodDatabaseService, and UserService.
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
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

@RestController
@RequestMapping("/api/admin")
class ContentAdminController(
    private val recipeService: RecipeService,
    private val knowledgeService: KnowledgeService,
    private val foodDatabaseService: FoodDatabaseService,
    private val userService: UserService
) {

    private val uploadDir = Paths.get("src/main/resources/static/images/uploads")

    init {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }
    }

    // ==================== Recipe Management ====================

    @PostMapping("/recipes")
    fun createRecipe(
        @RequestBody request: RecipeRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<RecipeResponse> {
        val user = userService.getUserByEmail(userDetails.username)
        val recipe = recipeService.createRecipe(request, user.id)
        return ResponseEntity.status(HttpStatus.CREATED).body(recipe)
    }

    @PutMapping("/recipes/{id}")
    fun updateRecipe(
        @PathVariable id: Long,
        @RequestBody request: RecipeRequest
    ): ResponseEntity<RecipeResponse> {
        return ResponseEntity.ok(recipeService.updateRecipe(id, request))
    }

    @DeleteMapping("/recipes/{id}")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<Void> {
        recipeService.deleteRecipe(id)
        return ResponseEntity.noContent().build()
    }

    // ==================== Knowledge Management ====================

    @PostMapping("/knowledge")
    fun createKnowledge(
        @RequestBody request: KnowledgeRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<KnowledgeResponse> {
        val user = userService.getUserByEmail(userDetails.username)
        val article = knowledgeService.createKnowledge(request, user.id)
        return ResponseEntity.status(HttpStatus.CREATED).body(article)
    }

    @PutMapping("/knowledge/{id}")
    fun updateKnowledge(
        @PathVariable id: Long,
        @RequestBody request: KnowledgeRequest
    ): ResponseEntity<KnowledgeResponse> {
        return ResponseEntity.ok(knowledgeService.updateKnowledge(id, request))
    }

    @DeleteMapping("/knowledge/{id}")
    fun deleteKnowledge(@PathVariable id: Long): ResponseEntity<Void> {
        knowledgeService.deleteKnowledge(id)
        return ResponseEntity.noContent().build()
    }

    // ==================== Food Database Management ====================

    @PostMapping("/food-database")
    fun createFoodEntry(
        @RequestBody request: FoodDatabaseRequest
    ): ResponseEntity<FoodDatabaseResponse> {
        val entry = foodDatabaseService.createFoodEntry(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(entry)
    }

    @PutMapping("/food-database/{id}")
    fun updateFoodEntry(
        @PathVariable id: Long,
        @RequestBody request: FoodDatabaseRequest
    ): ResponseEntity<FoodDatabaseResponse> {
        return ResponseEntity.ok(foodDatabaseService.updateFoodEntry(id, request))
    }

    @DeleteMapping("/food-database/{id}")
    fun deleteFoodEntry(@PathVariable id: Long): ResponseEntity<Void> {
        foodDatabaseService.deleteFoodEntry(id)
        return ResponseEntity.noContent().build()
    }

    // ==================== File Upload ====================

    /**
     * Upload an image file and return its URL path.
     * @param file uploaded image file
     * @return map containing the image URL path
     */
    @PostMapping("/upload-image")
    fun uploadImage(@RequestParam("file") file: MultipartFile): ResponseEntity<Map<String, String>> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest().body(mapOf("error" to "No file uploaded"))
        }

        val originalFilename = file.originalFilename ?: "image"
        val extension = originalFilename.substringAfterLast('.', "jpg")
        val newFilename = "${UUID.randomUUID()}.$extension"
        val targetPath = uploadDir.resolve(newFilename)

        file.inputStream.use { input ->
            Files.copy(input, targetPath)
        }

        val imageUrl = "images/uploads/$newFilename"
        return ResponseEntity.ok(mapOf("url" to imageUrl))
    }
}
