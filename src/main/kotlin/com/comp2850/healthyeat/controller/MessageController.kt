/**
 * MessageController handles chat message endpoints.
 * Both subscribers and professionals can use these endpoints to send and receive messages.
 * Depends on MessageService and UserService.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.controller

import com.comp2850.healthyeat.dto.MessageRequest
import com.comp2850.healthyeat.dto.MessageResponse
import com.comp2850.healthyeat.service.MessageService
import com.comp2850.healthyeat.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/messages")
class MessageController(
    private val messageService: MessageService,
    private val userService: UserService
) {

    @PostMapping
    fun sendMessage(
        @RequestBody request: MessageRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<MessageResponse> {
        val user = userService.getUserByEmail(userDetails.username)
        val message = messageService.sendMessage(user.id, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(message)
    }

    @GetMapping("/conversation")
    fun getConversation(
        @RequestParam otherId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<List<MessageResponse>> {
        val user = userService.getUserByEmail(userDetails.username)
        val messages = messageService.getConversation(user.id, otherId)
        return ResponseEntity.ok(messages)
    }

    @GetMapping("/unread")
    fun getUnreadCount(
        @RequestParam otherId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Map<String, Long>> {
        val user = userService.getUserByEmail(userDetails.username)
        val count = messageService.getUnreadCount(user.id, otherId)
        return ResponseEntity.ok(mapOf("count" to count))
    }

    @PostMapping("/mark-read")
    fun markAsRead(
        @RequestParam otherId: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Void> {
        val user = userService.getUserByEmail(userDetails.username)
        messageService.markAsRead(user.id, otherId)
        return ResponseEntity.noContent().build()
    }
}
