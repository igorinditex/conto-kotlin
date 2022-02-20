package com.ximedes.conto.integration.tests

import com.ximedes.conto.integration.AbstractIntegrationTest
import com.ximedes.conto.integration.pages.*
import org.junit.jupiter.api.Test

class LoginLogoutTest : AbstractIntegrationTest() {

    @Test
    fun testLoginLogout() {
        val username = createUniqueUsername()
        val homePage = open<LoginPage>("/")
            .clickSignupButton()
            .submitForm(SignupForm(username, defaultPassword))

        assertPage<HomePage>()

        val loginPage = logoutFromUI()
        assertPage<LoginPage>()

        loginPage.login(LoginForm(username, defaultPassword))
        assertPage<HomePage>()

        logoutFromUI()
        assertPage<LoginPage>()

    }

}