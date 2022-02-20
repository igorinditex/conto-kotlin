package com.ximedes.conto.api.security

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.security.web.context.HttpRequestResponseHolder
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.util.WebUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

const val API_TOKEN_HEADER = "X-Auth-TOKEN"

class JWTSecurityContextRepository(
    private val tokenizer: JWTUserTokenizer,
    private val userDetailsService: UserDetailsService
) : SecurityContextRepository {

    override fun loadContext(requestResponseHolder: HttpRequestResponseHolder): SecurityContext {
        val context = SecurityContextHolder.createEmptyContext()

        val jwt = requestResponseHolder.request.getHeader(API_TOKEN_HEADER)
        val username = tokenizer.validateTokenAndExtractUsername(jwt)
        if (username != null) {
            val userDetails = userDetailsService.loadUserByUsername(username)
            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            // TODO is this necessary?
            authentication.details = WebAuthenticationDetailsSource().buildDetails(requestResponseHolder.request)
            context.authentication = authentication
        }

        requestResponseHolder.response = SaveContextAsJWTOnUpdateOrErrorResponseWrapper(requestResponseHolder.response)

        return context
    }

    override fun saveContext(context: SecurityContext, request: HttpServletRequest, response: HttpServletResponse) {
        val wrapper = WebUtils.getNativeResponse(response, SaveContextAsJWTOnUpdateOrErrorResponseWrapper::class.java)
        if (wrapper != null) {
            wrapper.saveContext(context)
        } else {
            throw IllegalStateException("Cannot invoke saveContext on response $response. You must use the HttpRequestResponseHolder.response after invoking loadContext")
        }
    }

    override fun containsContext(request: HttpServletRequest) = request.getHeader(API_TOKEN_HEADER) != null

    inner class SaveContextAsJWTOnUpdateOrErrorResponseWrapper(
        private val response: HttpServletResponse
    ) : SaveContextOnUpdateOrErrorResponseWrapper(response, true) {

        public override fun saveContext(context: SecurityContext) {
            if (!isContextSaved && context.authentication is UsernamePasswordAuthenticationToken) {
                context.authentication.name?.let {
                    val token = tokenizer.createJWTForUsername(it)
                    response.addHeader(API_TOKEN_HEADER, token)
                }
            }
        }
    }

}