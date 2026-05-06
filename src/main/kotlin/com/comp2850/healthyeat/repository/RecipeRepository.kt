/**
 * RecipeRepository for accessing and managing Recipe entities.
 * Provides pagination, search by keyword, and filtering by category.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.Recipe
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RecipeRepository : JpaRepository<Recipe, Long> {
    @Query("SELECT r FROM Recipe r WHERE (:keyword IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND (:category IS NULL OR r.category = :category) AND r.id IS NOT NULL")
    fun searchAndFilter(@Param("keyword") keyword: String?, @Param("category") category: String?, pageable: Pageable): Page<Recipe>

    @Query("SELECT r FROM Recipe r WHERE r.id IS NOT NULL")
    fun findLatest(pageable: Pageable): Page<Recipe>

    @Query("SELECT r FROM Recipe r WHERE r.category = :category")
    fun findByCategory(category: String, pageable: Pageable): Page<Recipe>

    fun findByCreatedBy(createdBy: Long): List<Recipe>
}
