/**
 * HealthyEat - Health Diet and Nutrition Monitoring System
 * Main application entry point for the Spring Boot application.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HealthyEatApplication

fun main(args: Array<String>) {
    runApplication<HealthyEatApplication>(*args)
}
