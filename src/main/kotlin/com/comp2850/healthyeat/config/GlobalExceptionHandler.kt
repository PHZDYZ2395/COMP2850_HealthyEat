/**
 * GlobalExceptionHandler provides centralized exception handling for the application.
 * Catches common exceptions and returns appropriate HTTP error responses.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * Handle generic runtime exceptions.
     * @param ex the exception
     * @return error response with message and 400 status
     */
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (ex.message ?: "Runtime error occurred")))
    }

    /**
     * Handle illegal argument exceptions.
     * @param ex the exception
     * @return error response with message and 400 status
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (ex.message ?: "Illegal argument error occurred")))
    }
}
