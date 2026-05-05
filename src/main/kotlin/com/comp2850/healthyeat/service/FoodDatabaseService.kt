/**
 * FoodDatabaseService handles food database business logic including CRUD and pagination.
 * Depends on FoodDatabaseRepository.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.service

import com.comp2850.healthyeat.dto.FoodDatabaseRequest
import com.comp2850.healthyeat.dto.FoodDatabaseResponse
import com.comp2850.healthyeat.dto.PageResponse
import com.comp2850.healthyeat.repository.FoodDatabaseRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FoodDatabaseService(
    private val foodDatabaseRepository: FoodDatabaseRepository
) {

    /**
     * Get paginated food database entries with search and filter.
     * @param keyword search keyword
     * @param category filter category
     * @param page page number
     * @param size page size
     * @return PageResponse of FoodDatabaseResponse
     */
    @Transactional(readOnly = true)
    fun getFoodDatabase(keyword: String?, category: String?, page: Int, size: Int): PageResponse<FoodDatabaseResponse> {
        val pageable = PageRequest.of(page, size)
        val cleanKeyword = if (keyword.isNullOrBlank()) null else keyword
        val cleanCategory = if (category.isNullOrBlank()) null else category
        val pageResult = foodDatabaseRepository.searchAndFilter(cleanKeyword, cleanCategory, pageable)
        return PageResponse(
            content = pageResult.content.map { toResponse(it) },
            totalElements = pageResult.totalElements,
            totalPages = pageResult.totalPages,
            currentPage = pageResult.number,
            pageSize = pageResult.size
        )
    }

    /**
     * Get a single food database entry by ID.
     * @param id entry ID
     * @return FoodDatabaseResponse
     */
    @Transactional(readOnly = true)
    fun getFoodEntry(id: Long): FoodDatabaseResponse {
        val entry = foodDatabaseRepository.findById(id)
            .orElseThrow { RuntimeException("Food not found") }
        return toResponse(entry)
    }

    /**
     * Get popular food entries for homepage.
     * @param limit number of entries
     * @return list of FoodDatabaseResponse
     */
    @Transactional(readOnly = true)
    fun getPopularFoods(limit: Int): List<FoodDatabaseResponse> {
        val pageable = PageRequest.of(0, limit)
        return foodDatabaseRepository.findPopular(pageable).content.map { toResponse(it) }
    }

    /**
     * Search food entries by keyword for autocomplete suggestions.
     * @param keyword search keyword
     * @param limit max number of results
     * @return list of FoodSuggestion
     */
    @Transactional(readOnly = true)
    fun searchFoods(keyword: String?, limit: Int = 20): List<com.comp2850.healthyeat.dto.FoodSuggestion> {
        if (keyword.isNullOrBlank()) {
            return foodDatabaseRepository.findAll(PageRequest.of(0, limit)).content.map {
                com.comp2850.healthyeat.dto.FoodSuggestion(it.name, it.caloriesPer100g)
            }
        }
        return foodDatabaseRepository.findByNameContainingIgnoreCase(keyword, PageRequest.of(0, limit)).content.map {
            com.comp2850.healthyeat.dto.FoodSuggestion(it.name, it.caloriesPer100g)
        }
    }

    /**
     * Create a new food database entry.
     * @param request food data
     * @return FoodDatabaseResponse
     */
    @Transactional
    fun createFoodEntry(request: FoodDatabaseRequest): FoodDatabaseResponse {
        val entry = com.comp2850.healthyeat.domain.FoodDatabase(
            name = request.name,
            category = request.category,
            caloriesPer100g = request.caloriesPer100g,
            protein = request.protein,
            carbs = request.carbs,
            fat = request.fat,
            fiber = request.fiber,
            imageUrl = request.imageUrl
        )
        val saved = foodDatabaseRepository.save(entry)
        return toResponse(saved)
    }

    /**
     * Update an existing food database entry.
     * @param id entry ID
     * @param request updated food data
     * @return FoodDatabaseResponse
     */
    @Transactional
    fun updateFoodEntry(id: Long, request: FoodDatabaseRequest): FoodDatabaseResponse {
        val entry = foodDatabaseRepository.findById(id)
            .orElseThrow { RuntimeException("Food not found") }
        entry.name = request.name
        entry.category = request.category
        entry.caloriesPer100g = request.caloriesPer100g
        entry.protein = request.protein
        entry.carbs = request.carbs
        entry.fat = request.fat
        entry.fiber = request.fiber
        entry.imageUrl = request.imageUrl
        val saved = foodDatabaseRepository.save(entry)
        return toResponse(saved)
    }

    /**
     * Delete a food database entry.
     * @param id entry ID
     */
    @Transactional
    fun deleteFoodEntry(id: Long) {
        foodDatabaseRepository.deleteById(id)
    }

    private fun toResponse(entry: com.comp2850.healthyeat.domain.FoodDatabase) = FoodDatabaseResponse(
        id = entry.id!!,
        name = entry.name,
        category = entry.category,
        caloriesPer100g = entry.caloriesPer100g,
        protein = entry.protein,
        carbs = entry.carbs,
        fat = entry.fat,
        fiber = entry.fiber,
        imageUrl = entry.imageUrl,
        createdAt = entry.createdAt
    )
}
