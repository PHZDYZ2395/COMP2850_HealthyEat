/**
 * DTOs for knowledge article requests, responses, rating, and comments.
 * Contains KnowledgeRequest, KnowledgeResponse, KnowledgeDetailResponse.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.dto

import java.time.LocalDateTime

data class KnowledgeRequest(
    val title: String,
    val content: String,
    val summary: String,
    val category: String,
    val imageUrl: String?,
    val published: Boolean = true
)

data class KnowledgeResponse(
    val id: Long,
    val title: String,
    val summary: String,
    val imageUrl: String?,
    val category: String,
    val authorId: Long,
    val authorName: String,
    val published: Boolean,
    val createdAt: LocalDateTime,
    val averageRating: Double?,
    val ratingCount: Long
)

data class KnowledgeDetailResponse(
    val id: Long,
    val title: String,
    val content: String,
    val summary: String,
    val imageUrl: String?,
    val category: String,
    val authorId: Long,
    val authorName: String,
    val published: Boolean,
    val createdAt: LocalDateTime,
    val averageRating: Double?,
    val ratingCount: Long
)
