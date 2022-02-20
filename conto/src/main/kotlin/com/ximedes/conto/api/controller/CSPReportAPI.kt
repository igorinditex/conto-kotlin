package com.ximedes.conto.api.controller

import mu.KotlinLogging
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/csp-report")
class CSPReportAPI {
    private val logger = KotlinLogging.logger { }

    @PostMapping(consumes = ["application/csp-report"])
    fun reportCSPViolation(@RequestBody report: String?) {
        logger.warn { "A CSP violation has occurred: $report" }
    }

}