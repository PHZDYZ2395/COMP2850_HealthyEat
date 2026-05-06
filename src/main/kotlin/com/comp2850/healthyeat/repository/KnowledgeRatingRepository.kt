/**
 * KnowledgeRatingRepository for accessing and managing KnowledgeRating entities.
 * Provides methods to calculate average rating and check if user has rated.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.KnowledgeRating
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface KnowledgeRatingRepository : JpaRepository<KnowledgeRating, Long> {
    @Query("SELECT AVG(r.rating) FROM KnowledgeRating r WHERE r.articleId = :articleId")
    fun averageRating(@Param("articleId") articleId: Long): Double?

    @Query("SELECT COUNT(r) FROM KnowledgeRating r WHERE r.articleId = :articleId")
    fun countByArticleId(@Param("articleId") articleId: Long): Long

    fun findByUserIdAndArticleId(userId: Long, articleId: Long): KnowledgeRating?
    fun deleteByUserIdAndArticleId(userId: Long, articleId: Long)
}
