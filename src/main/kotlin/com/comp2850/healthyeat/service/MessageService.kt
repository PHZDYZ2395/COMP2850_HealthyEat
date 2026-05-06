/**
 * MessageService handles chat message business logic.
 * Provides sending, retrieving conversation, unread count, and mark-as-read.
 * Depends on MessageRepository, SubscriberProfessionalRepository, and UserService.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.service

import com.comp2850.healthyeat.domain.Message
import com.comp2850.healthyeat.dto.MessageRequest
import com.comp2850.healthyeat.dto.MessageResponse
import com.comp2850.healthyeat.repository.MessageRepository
import com.comp2850.healthyeat.repository.SubscriberProfessionalRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MessageService(
    private val messageRepository: MessageRepository,
    private val subscriberProfessionalRepository: SubscriberProfessionalRepository,
    private val userService: UserService
) {

    @Transactional
    fun sendMessage(senderId: Long, request: MessageRequest): MessageResponse {
        val message = messageRepository.save(
            Message(
                senderId = senderId,
                receiverId = request.receiverId,
                content = request.content
            )
        )
        val sender = userService.getUserById(senderId)
        return MessageResponse(
            id = message.id!!,
            senderId = message.senderId,
            senderName = sender.fullName,
            receiverId = message.receiverId,
            content = message.content,
            isRead = message.isRead,
            createdAt = message.createdAt
        )
    }

    @Transactional(readOnly = true)
    fun getConversation(user1: Long, user2: Long): List<MessageResponse> {
        return messageRepository.findConversation(user1, user2).map { msg ->
            val sender = userService.getUserById(msg.senderId)
            MessageResponse(
                id = msg.id!!,
                senderId = msg.senderId,
                senderName = sender.fullName,
                receiverId = msg.receiverId,
                content = msg.content,
                isRead = msg.isRead,
                createdAt = msg.createdAt
            )
        }
    }

    @Transactional(readOnly = true)
    fun getUnreadCount(receiverId: Long, senderId: Long): Long {
        return messageRepository.countUnread(receiverId, senderId)
    }

    @Transactional
    fun markAsRead(receiverId: Long, senderId: Long) {
        messageRepository.markAsRead(receiverId, senderId)
    }

    @Transactional(readOnly = true)
    fun getNutritionistId(subscriberId: Long): Long? {
        return subscriberProfessionalRepository.findBySubscriberId(subscriberId).firstOrNull()?.professionalId
    }

    @Transactional(readOnly = true)
    fun getSubscriberId(professionalId: Long, subscriberId: Long): Long? {
        val rel = subscriberProfessionalRepository.findByProfessionalIdAndSubscriberId(professionalId, subscriberId)
        return rel?.subscriberId
    }
}
