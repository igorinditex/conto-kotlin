package com.ximedes.conto.integration.tests

import com.ximedes.conto.integration.AbstractIntegrationTest
import com.ximedes.conto.integration.pages.LoginPage
import com.ximedes.conto.integration.pages.SignupForm
import com.ximedes.conto.integration.pages.open
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NewUserSetupTest : AbstractIntegrationTest() {

    @Test
    fun testDefaultAccountCreatedAndBonusApplied() {
        val homePage = open<LoginPage>("/")
            .clickSignupButton().submitForm(SignupForm(createUniqueUsername(), defaultPassword))

        val accounts = homePage.accountRows()
        val checking = accounts.first()
        assertEquals(1, accounts.size)
        assertEquals(100, checking.balance)
        assertEquals(0, checking.minimumBalance)
        assertEquals("Checking", checking.description)

        // Check if bonus transfer is visible
        val transfers = homePage.transferRows()
        assertEquals(1, transfers.size)
        assertEquals(100L, transfers.first().amount)

    }
}