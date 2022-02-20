package com.ximedes.conto.integration.pages

import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.page
import com.codeborne.selenide.SelenideElement
import org.junit.jupiter.api.Assertions.assertTrue
import org.openqa.selenium.support.FindBy

class LoginPage : BasePage() {

    @FindBy(id = "a-signup-link")
    private lateinit var signupLink: SelenideElement

    override fun isShowing() = `$`("#div-page-login").exists()

    fun clickSignupButton(): SignupPage {
        signupLink.click()
        return page(SignupPage::class.java)
    }

    fun <T : BasePage> login(form: LoginForm, resultPage: Class<T>): T {
        `$`("#input-username").value = form.username
        `$`("#input-password").value = form.password
        `$`("#button-submit-loginform").click()
        val page = page(resultPage)
        assertTrue(page.isShowing())
        return page
    }

    fun login(form: LoginForm): HomePage {
        return login(form, HomePage::class.java)
    }

}

class LoginForm(val username: String, val password: String)