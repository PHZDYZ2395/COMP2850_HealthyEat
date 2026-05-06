/**
 * KnowledgeCommentRepository for accessing and managing KnowledgeComment entities.
 * Provides methods to find comments by article ID and delete by user/article.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.KnowledgeComment
import org.springframework.data.jpa.repository.JpaRepository

interface KnowledgeCommentRepository : JpaRepository<KnowledgeComment, Long> {
    fun findByArticleIdOrderByCreatedAtDesc(articleId: Long): List<KnowledgeComment>
    fun findByUserId(userId: Long): List<KnowledgeComment>
    fun findByIdAndUserId(id: Long, userId: Long): KnowledgeComment?
}
