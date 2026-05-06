/**
 * Unit tests for FoodEntryService.
 * Tests CRUD operations for food entries.
 * Uses Mockito for mocking dependencies.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.service

import com.comp2850.healthyeat.domain.FoodEntry
import com.comp2850.healthyeat.dto.FoodEntryRequest
import com.comp2850.healthyeat.repository.FoodEntryRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.time.LocalDate
import java.util.*

class FoodEntryServiceTest {

    private lateinit var foodEntryRepository: FoodEntryRepository
    private lateinit var foodEntryService: FoodEntryService

    @BeforeEach
    fun setUp() {
        foodEntryRepository = mock(FoodEntryRepository::class.java)
        foodEntryService = FoodEntryService(foodEntryRepository)
    }

    @Test
    fun `createEntry saves and returns food entry`() {
        val request = FoodEntryRequest("Apple", "1 medium", 95, "Snack", LocalDate.of(2024, 1, 15))
        `when`(foodEntryRepository.save(any())).thenAnswer { invocation ->
            val entry = invocation.getArgument<FoodEntry>(0)
            entry.copy(id = 1L)
        }

        val result = foodEntryService.createEntry(1L, request)

        assertNotNull(result)
        assertEquals("Apple", result.foodName)
        assertEquals(95, result.calories)
        assertEquals(LocalDate.of(2024, 1, 15), result.entryDate)
        verify(foodEntryRepository).save(any())
    }

    @Test
    fun `updateEntry modifies existing entry`() {
        val existing = FoodEntry(
            id = 1L,
            userId = 1L,
            foodName = "Old Food",
            portionSize = "old",
            calories = 100,
            mealType = "Lunch",
            entryDate = LocalDate.of(2024, 1, 15)
        )
        `when`(foodEntryRepository.findById(1L)).thenReturn(Optional.of(existing))
        `when`(foodEntryRepository.save(any())).thenAnswer { invocation ->
            invocation.getArgument<FoodEntry>(0)
        }

        val request = FoodEntryRequest("New Food", "new portion", 200, "Dinner", LocalDate.of(2024, 1, 16))
        val result = foodEntryService.updateEntry(1L, request)

        assertEquals("New Food", result.foodName)
        assertEquals(200, result.calories)
        assertEquals(LocalDate.of(2024, 1, 16), result.entryDate)
    }

    @Test
    fun `updateEntry throws when not found`() {
        `when`(foodEntryRepository.findById(999L)).thenReturn(Optional.empty())
        val request = FoodEntryRequest("Food", null, 100, null, LocalDate.now())

        val ex = assertThrows<RuntimeException> {
            foodEntryService.updateEntry(999L, request)
        }
        assertEquals("Food entry not found", ex.message)
    }

    @Test
    fun `deleteEntry calls repository delete`() {
        foodEntryService.deleteEntry(1L)
        verify(foodEntryRepository).deleteById(1L)
    }

    @Test
    fun `getEntriesByUser returns entries for date filter`() {
        val entries = listOf(
            FoodEntry(id = 1L, userId = 1L, foodName = "Apple", calories = 95, entryDate = LocalDate.of(2024, 1, 15))
        )
        val date = LocalDate.of(2024, 1, 15)
        `when`(foodEntryRepository.findByUserIdAndEntryDate(1L, date)).thenReturn(entries)

        val result = foodEntryService.getEntriesByUser(1L, date)

        assertEquals(1, result.size)
        assertEquals("Apple", result[0].foodName)
    }

    @Test
    fun `getEntriesByUser returns all entries when no date`() {
        val entries = listOf(
            FoodEntry(id = 1L, userId = 1L, foodName = "Apple", calories = 95, entryDate = LocalDate.of(2024, 1, 15)),
            FoodEntry(id = 2L, userId = 1L, foodName = "Banana", calories = 105, entryDate = LocalDate.of(2024, 1, 14))
        )
        `when`(foodEntryRepository.findByUserIdOrderByEntryDateDesc(1L)).thenReturn(entries)

        val result = foodEntryService.getEntriesByUser(1L, null)

        assertEquals(2, result.size)
    }
}
