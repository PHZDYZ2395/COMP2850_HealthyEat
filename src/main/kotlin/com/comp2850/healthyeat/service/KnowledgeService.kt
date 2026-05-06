/**
 * KnowledgeService handles knowledge article business logic including CRUD, ratings, comments, and pagination.
 * Depends on KnowledgeArticleRepository, KnowledgeRatingRepository, KnowledgeCommentRepository, and UserService.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.service

import com.comp2850.healthyeat.domain.KnowledgeArticle
import com.comp2850.healthyeat.dto.*
import com.comp2850.healthyeat.repository.KnowledgeArticleRepository
import com.comp2850.healthyeat.repository.KnowledgeCommentRepository
import com.comp2850.healthyeat.repository.KnowledgeRatingRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class KnowledgeService(
    private val knowledgeArticleRepository: KnowledgeArticleRepository,
    private val knowledgeRatingRepository: KnowledgeRatingRepository,
    private val knowledgeCommentRepository: KnowledgeCommentRepository,
    private val userService: UserService
) {

    /**
     * Get paginated published knowledge articles with search and filter.
     * @param keyword search keyword
     * @param category filter category
     * @param page page number
     * @param size page size
     * @return PageResponse of KnowledgeResponse
     */
    @Transactional(readOnly = true)
    fun getKnowledge(keyword: String?, category: String?, page: Int, size: Int): PageResponse<KnowledgeResponse> {
        val pageable = PageRequest.of(page, size)
        val cleanKeyword = if (keyword.isNullOrBlank()) null else keyword
        val cleanCategory = if (category.isNullOrBlank()) null else category
        val pageResult = knowledgeArticleRepository.searchPublished(cleanKeyword, cleanCategory, pageable)
        return PageResponse(
            content = pageResult.content.map { toResponse(it) },
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
            currentPage = pageResult.number,
            pageSize = pageResult.size
        )
    }

    /**
     * Get a single knowledge article by ID.
     * @param id article ID
     * @return KnowledgeDetailResponse
     */
    @Transactional(readOnly = true)
    fun getKnowledgeDetail(id: Long): KnowledgeDetailResponse {
        val article = knowledgeArticleRepository.findById(id)
            .orElseThrow { RuntimeException("Article not found") }
        return toDetailResponse(article)
    }

    /**
     * Get latest published articles for homepage carousel.
     * @param limit number of articles
     * @return list of KnowledgeResponse
     */
    @Transactional(readOnly = true)
    fun getLatestKnowledge(limit: Int): List<KnowledgeResponse> {
        val pageable = PageRequest.of(0, limit)
        return knowledgeArticleRepository.findLatestPublished(pageable).content.map { toResponse(it) }
    }

    /**
     * Create a new knowledge article.
     * @param request article data
     * @param userId creator user ID
     * @return KnowledgeResponse
     */
    @Transactional
    fun createKnowledge(request: KnowledgeRequest, userId: Long): KnowledgeResponse {
        val article = KnowledgeArticle(
            title = request.title,
            content = request.content,
            summary = request.summary,
            category = request.category,
            imageUrl = request.imageUrl,
            authorId = userId,
            published = request.published
        )
        val saved = knowledgeArticleRepository.save(article)
        return toResponse(saved)
    }

    /**
     * Update an existing knowledge article.
     * @param id article ID
     * @param request updated article data
     * @return KnowledgeResponse
     */
    @Transactional
    fun updateKnowledge(id: Long, request: KnowledgeRequest): KnowledgeResponse {
        val article = knowledgeArticleRepository.findById(id)
            .orElseThrow { RuntimeException("Article not found") }
        article.title = request.title
        article.content = request.content
        article.summary = request.summary
        article.category = request.category
        article.imageUrl = request.imageUrl
        article.published = request.published
        val saved = knowledgeArticleRepository.save(article)
        return toResponse(saved)
    }

    /**
     * Delete a knowledge article.
     * @param id article ID
     */
    @Transactional
    fun deleteKnowledge(id: Long) {
        knowledgeArticleRepository.deleteById(id)
    }

    /**
     * Rate a knowledge article.
     * @param userId user ID
     * @param articleId article ID
     * @param request rating value
     */
    @Transactional
    fun rateArticle(userId: Long, articleId: Long, request: RatingRequest) {
        val existing = knowledgeRatingRepository.findByUserIdAndArticleId(userId, articleId)
        if (existing != null) {
            existing.rating = request.rating
            knowledgeRatingRepository.save(existing)
        } else {
            knowledgeRatingRepository.save(
                com.comp2850.healthyeat.domain.KnowledgeRating(
                    userId = userId,
                    articleId = articleId,
                    rating = request.rating
                )
            )
        }
    }

    /**
     * Get rating info for a knowledge article.
     * @param articleId article ID
     * @param userId optional user ID for user's own rating
     * @return RatingResponse
     */
    @Transactional(readOnly = true)
    fun getArticleRating(articleId: Long, userId: Long?): RatingResponse {
        val avg = knowledgeRatingRepository.averageRating(articleId) ?: 0.0
        val count = knowledgeRatingRepository.countByArticleId(articleId)
        val userRating = if (userId != null) {
            knowledgeRatingRepository.findByUserIdAndArticleId(userId, articleId)?.rating
        } else null
        return RatingResponse(averageRating = avg, ratingCount = count, userRating = userRating)
    }

    /**
     * Add a comment to a knowledge article.
     * @param userId user ID
     * @param articleId article ID
     * @param request comment text
     * @return CommentResponse
     */
    @Transactional
    fun addArticleComment(userId: Long, articleId: Long, request: CommentRequest): CommentResponse {
        val comment = com.comp2850.healthyeat.domain.KnowledgeComment(
            userId = userId,
            articleId = articleId,
            commentText = request.commentText
        )
        val saved = knowledgeCommentRepository.save(comment)
        val user = userService.getUserById(userId)
        return CommentResponse(
            id = saved.id!!,
            userId = saved.userId,
            userName = user.fullName,
            commentText = saved.commentText,
            createdAt = saved.createdAt
        )
    }

    /**
     * Get comments for a knowledge article.
     * @param articleId article ID
     * @return list of CommentResponse
     */
    @Transactional(readOnly = true)
    fun getArticleComments(articleId: Long): List<CommentResponse> {
        return knowledgeCommentRepository.findByArticleIdOrderByCreatedAtDesc(articleId).map { c ->
            val user = userService.getUserById(c.userId)
            CommentResponse(
                id = c.id!!,
                userId = c.userId,
                userName = user.fullName,
                commentText = c.commentText,
                createdAt = c.createdAt
            )
        }
    }

    /**
     * Delete a knowledge article comment.
     * @param commentId comment ID
     * @param userId user ID (for ownership check)
     */
    @Transactional
    fun deleteArticleComment(commentId: Long, userId: Long) {
        val comment = knowledgeCommentRepository.findByIdAndUserId(commentId, userId)
            ?: throw RuntimeException("Comment not found or not yours")
        knowledgeCommentRepository.delete(comment)
    }

    private fun toResponse(article: KnowledgeArticle): KnowledgeResponse {
        val author = userService.getUserById(article.authorId)
        val avgRating = knowledgeRatingRepository.averageRating(article.id!!)
        val ratingCount = knowledgeRatingRepository.countByArticleId(article.id!!)
        return KnowledgeResponse(
            id = article.id!!,
            title = article.title,
            summary = article.summary,
            imageUrl = article.imageUrl,
            category = article.category,
            authorId = article.authorId,
            authorName = author.fullName,
            published = article.published,
            createdAt = article.createdAt,
            averageRating = avgRating,
            ratingCount = ratingCount
        )
    }

    private fun toDetailResponse(article: KnowledgeArticle): KnowledgeDetailResponse {
        val author = userService.getUserById(article.authorId)
        val avgRating = knowledgeRatingRepository.averageRating(article.id!!)
        val ratingCount = knowledgeRatingRepository.countByArticleId(article.id!!)
        return KnowledgeDetailResponse(
            id = article.id!!,
            title = article.title,
            content = article.content,
            summary = article.summary,
            imageUrl = article.imageUrl,
            category = article.category,
            authorId = article.authorId,
            authorName = author.fullName,
            published = article.published,
            createdAt = article.createdAt,
            averageRating = avgRating,
            ratingCount = ratingCount
        )
    }
}
