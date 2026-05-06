/**
 * KnowledgeArticleRepository for accessing and managing KnowledgeArticle entities.
 * Provides pagination, search by keyword, filtering by category, and finding published articles.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.KnowledgeArticle
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface KnowledgeArticleRepository : JpaRepository<KnowledgeArticle, Long> {
    @Query("SELECT k FROM KnowledgeArticle k WHERE k.published = true AND (:keyword IS NULL OR LOWER(k.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(k.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND (:category IS NULL OR k.category = :category)")
    fun searchPublished(@Param("keyword") keyword: String?, @Param("category") category: String?, pageable: Pageable): Page<KnowledgeArticle>

    @Query("SELECT k FROM KnowledgeArticle k WHERE k.published = true ORDER BY k.createdAt DESC")
    fun findLatestPublished(pageable: Pageable): Page<KnowledgeArticle>

    fun findByAuthorIdOrderByCreatedAtDesc(authorId: Long): List<KnowledgeArticle>
}
