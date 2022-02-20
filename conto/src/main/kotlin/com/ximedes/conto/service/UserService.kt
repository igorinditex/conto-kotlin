package com.ximedes.conto.service

import com.ximedes.conto.asCanonicalUsername
import com.ximedes.conto.db.UserMapper
import com.ximedes.conto.domain.AdminUserCreatedEvent
import com.ximedes.conto.domain.Role
import com.ximedes.conto.domain.User
import com.ximedes.conto.domain.UserSignedUpEvent
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.Normalizer

const val ADMIN_USERNAME = "admin"
private const val ADMIN_PASSWORD = "admin"

@Service
@Transactional
class UserService(
    private val userMapper: UserMapper,
    private val encoder: PasswordEncoder,
    private val eventPublisher: ApplicationEventPublisher
) : UserDetailsService {

    private val logger = KotlinLogging.logger { }

    val loggedInUser: User?
        get() = SecurityContextHolder.getContext().authentication?.principal as User


    @EventListener
    fun onContextRefreshedEvent(e: ContextRefreshedEvent?) {
        logger.info("Creating admin user with username '$ADMIN_USERNAME' and default password")
        val admin = User(ADMIN_USERNAME, encoder.encode(ADMIN_PASSWORD), Role.ADMIN)
        userMapper.insertUser(admin, ADMIN_USERNAME)
        eventPublisher.publishEvent(AdminUserCreatedEvent(this, ADMIN_USERNAME))
    }

    override fun loadUserByUsername(username: String): UserDetails {
        return userMapper.findByUsername(username) ?: throw UsernameNotFoundException(username)
    }

    fun findByUsername(username: String): User? {
        return userMapper.findByUsername(username)
    }

    fun isCommonPassword(password: String?) = password?.let {
        userMapper.isCommonPassword(Normalizer.normalize(password, Normalizer.Form.NFKC))
    } ?: true

    fun signupAndLogin(username: String, password: String): User {
        val user = User(username, encoder.encode(password), Role.USER)
        userMapper.insertUser(user, username.asCanonicalUsername())

        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(user, password, user.authorities)

        eventPublisher.publishEvent(UserSignedUpEvent(this, user.getUsername()))
        return user
    }
}