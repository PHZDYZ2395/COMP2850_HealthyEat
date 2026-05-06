/**
 * Recipe entity representing a healthy recipe with ingredients, instructions, and metadata.
 * Stores recipe title, description, ingredients, cooking instructions, category, difficulty, and image.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "recipes")
data class Recipe(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Column(columnDefinition = "TEXT")
    var ingredients: String,

    @Column(columnDefinition = "TEXT")
    var instructions: String,

    @Column(name = "image_url")
    var imageUrl: String? = null,

    @Column(nullable = false)
    var category: String,

    @Column(nullable = false)
    var difficulty: String,

    @Column(name = "prep_time")
    var prepTime: Int,

    @Column(nullable = false)
    var servings: Int,

    @Column(nullable = false)
    var calories: Int,

    @Column(name = "created_by")
    val createdBy: Long,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
