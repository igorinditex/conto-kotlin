package com.ximedes.conto.api.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class APIAuthenticationFilter(authenticationManager: AuthenticationManager) :
    AbstractAuthenticationProcessingFilter("/api/login") {

    init {
        super.setAuthenticationManager(authenticationManager)
        setAuthenticationSuccessHandler { _: HttpServletRequest, response: HttpServletResponse, _: Authentication ->
            response.status = HttpServletResponse.SC_OK
        }
        setAuthenticationFailureHandler { _: HttpServletRequest, response: HttpServletResponse, _: AuthenticationException ->
            response.status = HttpServletResponse.SC_BAD_REQUEST
        }
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        if (request.method != "POST") {
            throw AuthenticationServiceException("Authentication method not supported: " + request.method)
        }
        val username = request.getParameter("username")
        val password = request.getParameter("password")
        val authToken = UsernamePasswordAuthenticationToken(username, password)
        return authenticationManager.authenticate(authToken)
    }


}