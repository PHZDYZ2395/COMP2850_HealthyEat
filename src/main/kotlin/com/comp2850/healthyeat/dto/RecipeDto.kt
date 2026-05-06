/**
 * DTOs for recipe requests, responses, rating, and comments.
 * Contains RecipeRequest, RecipeResponse, PageResponse, RatingResponse, and CommentResponse.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.dto

import java.time.LocalDateTime

data class RecipeRequest(
    val title: String,
    val description: String,
    val ingredients: String,
    val instructions: String,
    val imageUrl: String?,
    val category: String,
    val difficulty: String,
    val prepTime: Int,
    val servings: Int,
    val calories: Int
)

data class RecipeResponse(
    val id: Long,
    val title: String,
    val description: String,
    val ingredients: String,
    val instructions: String,
    val imageUrl: String?,
    val category: String,
    val difficulty: String,
    val prepTime: Int,
    val servings: Int,
    val calories: Int,
    val createdBy: Long,
    val createdByName: String,
    val createdAt: LocalDateTime,
    val averageRating: Double?,
    val ratingCount: Long
)

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val currentPage: Int,
    val pageSize: Int
)

data class RatingRequest(
    val rating: Int
)

data class RatingResponse(
    val averageRating: Double,
    val ratingCount: Long,
    val userRating: Int?
)

data class CommentRequest(
    val commentText: String
)

data class CommentResponse(
    val id: Long,
    val userId: Long,
    val userName: String,
    val commentText: String,
    val createdAt: LocalDateTime
)
