package com.ximedes.conto.api.security

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.ximedes.conto.UserBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.context.HttpRequestResponseHolder

class JWTSecurityContextRepositoryTest {

    val tokenizer = mock<JWTUserTokenizer>()
    val userDetailsService = mock<UserDetailsService>()
    val repo = JWTSecurityContextRepository(tokenizer, userDetailsService)

    val request = MockHttpServletRequest()
    val response = MockHttpServletResponse()
    val holder = HttpRequestResponseHolder(request, response)
    lateinit var savedContext: SecurityContext

    @BeforeEach
    fun setup() {
        savedContext = SecurityContextHolder.getContext()
    }

    @AfterEach
    fun restoreContext() = SecurityContextHolder.setContext(savedContext)

    @Test
    fun `an empty context is stored when no JWT header is present`() {
        assertFalse(repo.containsContext(request))

        val shouldBeEmptyContext = repo.loadContext(holder)
        assertNull(shouldBeEmptyContext.authentication)

        assertTrue(holder.response is JWTSecurityContextRepository.SaveContextAsJWTOnUpdateOrErrorResponseWrapper)
    }

    @Test
    fun `an empty context is stored when the JWT is malformed`() {
        request.addHeader(API_TOKEN_HEADER, "not_a_jwt")
        whenever(tokenizer.validateTokenAndExtractUsername("not_a_jwt")).thenReturn(null)

        val shouldBeEmptyContext = repo.loadContext(holder)
        assertNull(shouldBeEmptyContext.authentication)

        assertTrue(holder.response is JWTSecurityContextRepository.SaveContextAsJWTOnUpdateOrErrorResponseWrapper)
    }

    @Test
    fun `when the token is valid it stores a principal in the context`() {
        val user = UserBuilder.build()
        request.addHeader(API_TOKEN_HEADER, "a_valid_token")
        whenever(tokenizer.validateTokenAndExtractUsername("a_valid_token")).thenReturn(user.username)
        whenever(userDetailsService.loadUserByUsername(user.username)).thenReturn(user)

        assertTrue(repo.containsContext(request))
        val context = repo.loadContext(holder)
        assertEquals(user, context.authentication.principal)

        assertTrue(holder.response is JWTSecurityContextRepository.SaveContextAsJWTOnUpdateOrErrorResponseWrapper)
    }

    @Test
    fun `when the token is valid it stores a JWT in the response`() {
        val user = UserBuilder.build()
        request.addHeader(API_TOKEN_HEADER, "a_valid_token")
        whenever(tokenizer.validateTokenAndExtractUsername("a_valid_token")).thenReturn(user.username)
        whenever(userDetailsService.loadUserByUsername(user.username)).thenReturn(user)
        whenever(tokenizer.createJWTForUsername(user.username)).thenReturn("a_fresh_token")

        assertTrue(repo.containsContext(request))
        val context = repo.loadContext(holder)
        repo.saveContext(context, holder.request, holder.response)

        assertEquals("a_fresh_token", response.getHeaderValue(API_TOKEN_HEADER))
    }
}