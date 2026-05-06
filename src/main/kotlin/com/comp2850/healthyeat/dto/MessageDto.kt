/**
 * DTOs for chat messages.
 * Contains MessageRequest and MessageResponse data classes.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.dto

import java.time.LocalDateTime

data class MessageRequest(
    val receiverId: Long,
    val content: String
)

data class MessageResponse(
    val id: Long,
    val senderId: Long,
    val senderName: String,
    val receiverId: Long,
    val content: String,
    val isRead: Boolean,
    val createdAt: LocalDateTime
)
