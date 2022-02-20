package com.ximedes.conto.integration.tests

import com.ximedes.conto.integration.AbstractIntegrationTest
import com.ximedes.conto.integration.pages.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SignupFormValidationTest : AbstractIntegrationTest() {

    val validUsername = createUniqueUsername()
    val validPassword = defaultPassword
    lateinit var signupPage: SignupPage

    @Test
    fun `happy flow`() {
        val signupPage = open<LoginPage>("/").clickSignupButton()
        signupPage.submitForm(SignupForm(createUniqueUsername(), validPassword))
        assertPage<HomePage>()
    }

    @Test
    fun `invalid username`() {
        // Empty
        checkFormValidation(SignupForm("", validPassword, validPassword), 1)
        // HTML, special character
        checkFormValidation(SignupForm("<p>Yo!</p>", validPassword, validPassword), 1)
        // Too long
        checkFormValidation(SignupForm("123456789012345678901", validPassword, validPassword), 1)
        // Control character
        checkFormValidation(SignupForm("controlchar\u008D", validPassword, validPassword), 1)
        // Symbol
        checkFormValidation(SignupForm("symbol™", validPassword, validPassword), 1)
        // No white space
        checkFormValidation(SignupForm("user name", validPassword, validPassword), 1)
    }

    @Test
    fun `invalid password`() {
        // Too short, missing digit, missing punctuation
        checkFormValidation(SignupForm(validUsername, ""), 1)
        // Too short
        checkFormValidation(SignupForm(validUsername, "1_3_5_7"), 1)
        // Control character
        checkFormValidation(SignupForm(validUsername, "jopo\u008Disok"), 1)
        // Hidden white space
        checkFormValidation(SignupForm(validUsername, "jopo\u200Bisok"), 1)
        // Passwords not equal
        checkFormValidation(SignupForm(validUsername, validPassword, "anotherpwd"), 1)
    }

    private fun checkFormValidation(form: SignupForm, expectedNumberOfErrors: Int) {
        val signupPage = open<LoginPage>("/").clickSignupButton()
        signupPage.submitForm<SignupPage>(form)
        assertPage<SignupPage>()
        assertEquals(expectedNumberOfErrors, signupPage.errorMessages.size)
    }

}