package com.ximedes.conto.web

import com.ximedes.conto.AccountFormBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class CreateAccountFormValidationsTest {

    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `default form is valid`() {
        assertViolations(0) {}
    }


    @Test
    fun `single letter description is valid`() {
        assertViolations(0) { description = "a" }
    }

    @Test
    fun `null description is invalid`() {
        assertViolations(1) { description = null }
    }

    @Test
    fun `maximum description length is 64`() {
        assertViolations(0) {
            description = "1_________2_________3_________4_________5_________6_________1234"
        }
        assertViolations(1) {
            description = "1_________2_________3_________4_________5_________6_________12345"
        }

    }

    private fun assertViolations(number: Int, builder: AccountFormBuilder.() -> Unit) {
        val form = AccountFormBuilder.build(builder)
        val violations = validator.validate(form)
        assertEquals(number, violations.size)
    }
}