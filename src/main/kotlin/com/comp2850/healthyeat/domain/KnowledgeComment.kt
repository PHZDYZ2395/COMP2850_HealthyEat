/**
 * KnowledgeComment entity representing a user's comment on a knowledge article.
 * Stores user ID, article ID, comment text, and timestamp.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "knowledge_comments")
data class KnowledgeComment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "article_id", nullable = false)
    val articleId: Long,

    @Column(name = "comment_text", columnDefinition = "TEXT", nullable = false)
    var commentText: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
