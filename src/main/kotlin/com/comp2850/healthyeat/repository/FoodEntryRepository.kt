/**
 * FoodEntryRepository for accessing and managing FoodEntry entities.
 * Provides methods to find entries by user, date, and trends.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.FoodEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface FoodEntryRepository : JpaRepository<FoodEntry, Long> {
    fun findByUserId(userId: Long): List<FoodEntry>
    fun findByUserIdAndEntryDate(userId: Long, entryDate: LocalDate): List<FoodEntry>
    fun findByUserIdOrderByEntryDateDesc(userId: Long): List<FoodEntry>

    @Query("SELECT f.entryDate as entryDate, SUM(f.calories) as totalCalories FROM FoodEntry f WHERE f.userId = :userId AND f.entryDate >= :startDate GROUP BY f.entryDate ORDER BY f.entryDate")
    fun findCaloriesTrend(@Param("userId") userId: Long, @Param("startDate") startDate: LocalDate): List<Array<Any>>
}
