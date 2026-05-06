/**
 * MessageRepository for accessing and managing Message entities.
 * Provides methods to find messages between two users and count unread messages.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.repository

import com.comp2850.healthyeat.domain.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MessageRepository : JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.senderId = :user1 AND m.receiverId = :user2) OR (m.senderId = :user2 AND m.receiverId = :user1) ORDER BY m.createdAt ASC")
    fun findConversation(@Param("user1") user1: Long, @Param("user2") user2: Long): List<Message>

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :receiverId AND m.senderId = :senderId AND m.isRead = false")
    fun countUnread(@Param("receiverId") receiverId: Long, @Param("senderId") senderId: Long): Long

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiverId = :receiverId AND m.senderId = :senderId AND m.isRead = false")
    fun markAsRead(@Param("receiverId") receiverId: Long, @Param("senderId") senderId: Long)
}
