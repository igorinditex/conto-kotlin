package com.ximedes.conto.integration.tests

import com.ximedes.conto.integration.AbstractIntegrationTest
import com.ximedes.conto.integration.pages.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.*

class TransferTest : AbstractIntegrationTest() {

    val username1 = createUniqueUsername()
    val password1 = defaultPassword
    val username2 = createUniqueUsername()
    val password2 = defaultPassword

    lateinit var account1: String
    lateinit var account2: String

    @BeforeAll
    fun `create users`() {
        open<LoginPage>("/").clickSignupButton().submitForm(username1, password1).also {
            account1 = it.findAccountIDsForDescription("Checking").first()
        }
        logoutFromUI()
        open<LoginPage>("/").clickSignupButton().submitForm(username2, password2).also {
            account2 = it.findAccountIDsForDescription("Checking").first()
        }
        logoutFromUI()
    }

    @Test
    fun createAndFindTransfer() {
        open<LoginPage>("/")
            .login(LoginForm(username1, password1))
            .clickCreateTransferButton()
            .transfer(
                TransferForm(
                    debitAccountID = account1,
                    creditAccountID = account2,
                    amount = 25,
                    description = "Test"
                )
            )

        var homePage = assertPage<HomePage>()

        // Check if balances are correct
        val accounts1 = homePage.accountRows()
        assertEquals(75, accounts1.single().balance)
        val transfers1 = homePage.transferRows()
        assertEquals(2, transfers1.size)
        // First in the list is the latest
        assertEquals(-25, transfers1[0].amount)
        assertEquals(100, transfers1[1].amount)

        logoutFromUI()
        homePage = open<LoginPage>("/")
            .login(LoginForm(username2, password2))
        val accounts2 = homePage.accountRows()
        assertEquals(125, accounts2.single().balance)
        val transfers2 = homePage.transferRows()
        assertEquals(2, transfers2.size)
        // First in the list is the latest
        assertEquals(25, transfers2[0].amount)
        assertEquals(100, transfers2[1].amount)
    }

    @Test
    fun `transfers can be searched`() {
        var homePage = open<LoginPage>("/")
            .login(LoginForm(username2, password2))

        val ids = (1..10).map { UUID.randomUUID().toString() }
        ids.forEach { guid ->
            val transferFound = homePage.clickCreateTransferButton()
                .transfer(
                    TransferForm(
                        debitAccountID = account2,
                        creditAccountID = account1,
                        amount = 1,
                        description = guid
                    )
                ).transferRows(guid).first()
            assertEquals(-1, transferFound.amount)
        }

        logoutFromUI()

        homePage = open<LoginPage>("/")
            .login(LoginForm(username1, password1))
        ids.forEach { guid ->
            val transferFound = homePage.transferRows(guid).first()
            assertEquals(1, transferFound.amount)
        }


    }

}