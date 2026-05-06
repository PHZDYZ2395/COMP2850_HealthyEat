/**
 * KnowledgeArticle entity representing a health knowledge article.
 * Stores article title, content, summary, category, image, publication status.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "knowledge_articles")
data class KnowledgeArticle(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var content: String,

    @Column(columnDefinition = "TEXT")
    var summary: String,

    @Column(nullable = false)
    var category: String,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @Column(name = "author_id")
    val authorId: Long,

    @Column(nullable = false)
    var published: Boolean = true,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
