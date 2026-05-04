/**
 * SecurityConfig configures Spring Security for the application.
 * Sets up HTTP security rules, CORS, CSRF, and JWT filter chain.
 * Depends on JwtAuthenticationFilter for token-based authentication.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    /**
     * Configure password encoder using BCrypt.
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    /**
     * Expose AuthenticationManager bean.
     * @param authenticationConfiguration Spring's auth config
     * @return AuthenticationManager instance
     */
    @Bean
    fun authenticationManager(
        authenticationConfiguration: AuthenticationConfiguration
    ): AuthenticationManager = authenticationConfiguration.authenticationManager

    /**
     * Configure security filter chain with HTTP rules.
     * @param http HttpSecurity builder
     * @return configured SecurityFilterChain
     */
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    "/api/auth/**",
                    "/api/recipes/**",
                    "/api/knowledge/**",
                    "/api/food-database/**",
                    "/api/users/me",
                    "/login.html",
                    "/register.html",
                    "/index.html",
                    "/dashboard.html",
                    "/clients.html",
                    "/client-diary.html",
                    "/admin.html",
                    "/recipes.html",
                    "/recipe-detail.html",
                    "/knowledge.html",
                    "/knowledge-article.html",
                    "/food-database.html",
                    "/food-diary.html",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/h2-console/**",
                    "/"
                ).permitAll()
                auth.requestMatchers("/api/admin/upload-image").hasAnyRole("ADMIN", "PROFESSIONAL")
                auth.requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "PROFESSIONAL")
                auth.requestMatchers("/api/professional/**").hasRole("PROFESSIONAL")
                auth.requestMatchers("/api/food-entries/**", "/api/advice").hasRole("SUBSCRIBER")
                auth.requestMatchers("/api/subscribers/**").hasRole("SUBSCRIBER")
                auth.requestMatchers("/api/messages/**").authenticated()
                auth.anyRequest().authenticated()
            }
            .headers { headers ->
                headers.frameOptions { it.sameOrigin() }
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        return org.springframework.web.cors.UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}
