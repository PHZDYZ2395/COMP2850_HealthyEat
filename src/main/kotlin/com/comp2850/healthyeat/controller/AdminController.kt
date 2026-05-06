/**
 * AdminController handles admin management endpoints.
 * Provides user management, professional statistics, and system statistics.
 * Depends on UserService, AdminService, and FoodEntryService.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.controller

import com.comp2850.healthyeat.domain.UserRole
import com.comp2850.healthyeat.dto.*
import com.comp2850.healthyeat.service.AdminService
import com.comp2850.healthyeat.service.FoodEntryService
import com.comp2850.healthyeat.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class AdminController(
    private val userService: UserService,
    private val adminService: AdminService,
    private val foodEntryService: FoodEntryService
) {

    /**
     * Get all users, optionally filtered by role.
     * @param role optional role filter
     * @return list of admin user responses
     */
    @GetMapping("/users")
    fun getAllUsers(@RequestParam(required = false) role: UserRole?): ResponseEntity<List<AdminUserResponse>> {
        val users = userService.getAllUsers(role)
        return ResponseEntity.ok(users)
    }

    /**
     * Enable or disable a user.
     * @param id the user ID
     * @param request enable/disable request
     * @return no content
     */
    @PutMapping("/users/{id}/enable")
    fun setUserEnabled(
        @PathVariable id: Long,
        @RequestBody request: EnableUserRequest
    ): ResponseEntity<Void> {
        userService.setUserEnabled(id, request.enabled)
        return ResponseEntity.noContent().build()
    }

    /**
     * Change a user's role.
     * @param id the user ID
     * @param request role change request
     * @return no content
     */
    @PutMapping("/users/{id}/role")
    fun setUserRole(
        @PathVariable id: Long,
        @RequestBody request: RoleChangeRequest
    ): ResponseEntity<Void> {
        userService.setUserRole(id, request.role)
        return ResponseEntity.noContent().build()
    }

    /**
     * Delete a user.
     * @param id the user ID
     * @return no content
     */
    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }

    /**
     * Get all professionals with their client counts.
     * @return list of professional stats
     */
    @GetMapping("/professionals")
    fun getProfessionals(): ResponseEntity<List<ProfessionalStats>> {
        val stats = adminService.getProfessionalsWithStats()
        return ResponseEntity.ok(stats)
    }

    /**
     * Get system-wide statistics.
     * @return system stats
     */
    @GetMapping("/stats")
    fun getSystemStats(): ResponseEntity<SystemStats> {
        val stats = adminService.getSystemStats()
        return ResponseEntity.ok(stats)
    }
}
