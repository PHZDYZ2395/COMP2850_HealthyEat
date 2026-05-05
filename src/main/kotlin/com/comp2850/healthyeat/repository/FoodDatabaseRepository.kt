/**
 * FoodDatabaseRepository for accessing and managing FoodDatabase entities.
 * Provides pagination, search by keyword, and filtering by category.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.FoodDatabase
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FoodDatabaseRepository : JpaRepository<FoodDatabase, Long> {
    @Query("SELECT f FROM FoodDatabase f WHERE (:keyword IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND (:category IS NULL OR f.category = :category)")
    fun searchAndFilter(@Param("keyword") keyword: String?, @Param("category") category: String?, pageable: Pageable): Page<FoodDatabase>

    @Query("SELECT f FROM FoodDatabase f ORDER BY f.caloriesPer100g DESC")
    fun findPopular(pageable: Pageable): Page<FoodDatabase>

    fun findByNameContainingIgnoreCase(name: String, pageable: Pageable): Page<FoodDatabase>
}
