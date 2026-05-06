/**
 * KnowledgeRating entity representing a user's rating for a knowledge article.
 * Stores user ID, article ID, rating value (1-5), and timestamp.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "knowledge_ratings")
data class KnowledgeRating(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "article_id", nullable = false)
    val articleId: Long,

    @Column(nullable = false)
    var rating: Int,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
