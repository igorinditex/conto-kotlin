package com.ximedes.conto.integration.tests

import com.ximedes.conto.integration.AbstractIntegrationTest
import com.ximedes.conto.integration.pages.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateAccountFormValidationTest : AbstractIntegrationTest() {
    lateinit var page: CreateAccountPage

    val username = createUniqueUsername()
    val password = defaultPassword

    @BeforeAll
    fun createAccount() {
        open<LoginPage>("/")
            .clickSignupButton()
            .submitForm(SignupForm(username, password))

        logoutFromUI()
    }

    @BeforeEach
    fun navigateToCreateAccountPage() {
        page = open<LoginPage>("/").login(LoginForm(username, password)).clickCreateAccountButton()
    }


    @Test
    fun `description at max length should be OK`() {
        page.createAccount("1_________2_________3_________4_________5_________6_________1234")
        assertPage<HomePage>()
    }

    @Test
    fun `invalid account description`() {
        // Empty
        checkFormValidation("", 1)
        // Too long
        checkFormValidation("1_________2_________3_________4_________5_________6_________12345", 1)
        // HTML input
        checkFormValidation("<p>Yo!</p>", 1)
    }

    private fun checkFormValidation(description: String, expectedNumberOfErrors: Int) {
        val cap = page.createAccount<CreateAccountPage>(description)
        assertPage<CreateAccountPage>()
        assertEquals(expectedNumberOfErrors, cap.errorMessages.size)
    }

}