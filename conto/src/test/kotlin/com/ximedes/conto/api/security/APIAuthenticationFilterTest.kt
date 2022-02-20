package com.ximedes.conto.api.security

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException

class APIAuthenticationFilterTest {

    val authenticationManager = mock<AuthenticationManager>()
    val filter = APIAuthenticationFilter(authenticationManager)
    val request = MockHttpServletRequest()
    val response = MockHttpServletResponse()
    val tokenCaptor = argumentCaptor<UsernamePasswordAuthenticationToken>()

    @Test
    fun `it only accepts POST requests`() {
        listOf("GET", "PUT", "OPTIONS", "DELETE", "HEAD").forEach {
            assertThrows<AuthenticationException> {
                filter.attemptAuthentication(request.apply { method = it }, response)
            }
        }
    }

    @Test
    fun `it delegates to the AuthenticationManager using the right data from HTTP headers`() {
        request.apply {
            method = "POST"
            addParameter("username", "_username")
            addParameter("password", "_password")
        }
        val token = UsernamePasswordAuthenticationToken(null, null)
        whenever(authenticationManager.authenticate(tokenCaptor.capture())).thenReturn(token)
        val authentication = filter.attemptAuthentication(request, response)

        assertSame(token, authentication)
        assertEquals("_username", tokenCaptor.firstValue.name)
        assertEquals("_password", tokenCaptor.firstValue.credentials)
    }

}