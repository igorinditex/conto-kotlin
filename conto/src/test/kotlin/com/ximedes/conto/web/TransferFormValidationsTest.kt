package com.ximedes.conto.web

import com.ximedes.conto.AccountFormBuilder
import com.ximedes.conto.TransferFormBuilder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator

class TransferFormValidationsTest {
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `default form is valid`() {
        assertViolations(0) {}
    }

    @Test
    fun `account IDs can not be be null`() = assertViolations(2) {
        fromAccountID = null
        toAccountID = null
    }

    @Test
    fun `account IDs can not be be empty`() = assertViolations(2) {
        fromAccountID = ""
        toAccountID = ""
    }

    @Test
    fun `account IDs may be 40 long`() = assertViolations(0) {
        fromAccountID = (1..40).joinToString("") { "x" }
        toAccountID = (1..40).joinToString("") { "x" }
    }

    @Test
    fun `account IDs may not be 41 long`() = assertViolations(2) {
        fromAccountID = (1..41).joinToString("") { "x" }
        toAccountID = (1..41).joinToString("") { "x" }
    }

    @Test
    fun `amount may not be zero`() = assertViolations(1) {
        amount = 0L
    }

    @Test
    fun `amount may not be negative`() = assertViolations(1) {
        amount = -1L
    }

    @Test
    fun `description may not be null`() = assertViolations(1) {
        description = null
    }

    @Test
    fun `description may not be empty`() = assertViolations(1) {
        description = ""
    }

    @Test
    fun `description may be a single character long`() = assertViolations(0) {
        description = "x"
    }

    @Test
    fun `description may be 512 long`() = assertViolations(0) {
        description = (1..512).joinToString("") { "x" }
    }

    @Test
    fun `description may not be 513 long`() = assertViolations(1) {
        description = (1..513).joinToString("") { "x" }
    }



    private fun assertViolations(number: Int, builder: TransferFormBuilder.() -> Unit) {
        val form = TransferFormBuilder.build(builder)
        val violations = validator.validate(form)
        assertEquals(number, violations.size)
    }


}