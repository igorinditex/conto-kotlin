package com.ximedes.conto.web

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class MvcConfig : WebMvcConfigurer {

    /**
     * In this configuration method we configure a custom login page
     * to replace the default one that comes with Spring Security.
     *
     * This is achieved by mapping the /pubic/login url to our custom view
     * implemented in login.html in resources/templates.
     */
    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/public/login").setViewName("login")
    }
}