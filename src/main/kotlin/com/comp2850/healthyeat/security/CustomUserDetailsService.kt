/**
 * CustomUserDetailsService implements Spring Security's UserDetailsService.
 * Loads user details from the database for authentication.
 * Depends on UserRepository to fetch user entities.
 * @author COMP2850 Team
 */
package com.comp2850.healthyeat.security

import com.comp2850.healthyeat.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    /**
     * Load user details by email for Spring Security authentication.
     * @param email the email address to look up
     * @return UserDetails object for authentication
     * @throws UsernameNotFoundException if user is not found
     */
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
            .orElseThrow { UsernameNotFoundException("User not found: $email") }

        return org.springframework.security.core.userdetails.User(
            user.email,
            user.passwordHash,
            user.enabled,
            true, true, true,
            listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
        )
    }
}
