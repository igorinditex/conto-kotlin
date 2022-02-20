package com.ximedes.conto.web.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers("/h2-console/**")
    }

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
            .antMatchers("/public/**", "/favicon.ico").permitAll()
            .anyRequest().authenticated()
            .and().formLogin().loginPage("/public/login")
            .and().logout().logoutUrl("/public/logout")
            .and()
            .headers()
            // The hash in the CSP header is for an inline script in `navigation.html`
            .contentSecurityPolicy("default-src 'none'; script-src 'self' 'sha256-5dSrpVfVwiHl/79skWyIsVvfbPRqht26L05ensmI1dY='; style-src 'self' 'unsafe-inline'; font-src 'self'; img-src 'self'; report-uri /api/csp-report")
            .and()

    }
}