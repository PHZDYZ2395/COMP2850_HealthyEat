/**
 * DTOs for professional advice requests and responses.
 * Contains AdviceRequest and AdviceResponse data classes.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.dto

import java.time.LocalDateTime

data class AdviceRequest(
    val adviceText: String
)

data class AdviceResponse(
    val id: Long,
    val professionalId: Long,
    val professionalName: String,
    val subscriberId: Long,
    val adviceText: String,
    val createdAt: LocalDateTime
)
