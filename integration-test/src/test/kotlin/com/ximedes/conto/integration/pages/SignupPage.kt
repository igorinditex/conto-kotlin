package com.ximedes.conto.integration.pages

import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.`$`

class SignupPage : BasePage() {

    val errorMessages
        get() = `$`("#form-signup-errors").text().split("\n")

    override fun isShowing() = `$`("#form-signup").exists()

    inline fun <reified T : BasePage> submitForm(signupForm: SignupForm): T {
        `$`("#input-username").value = signupForm.username
        `$`("#input-password").value = signupForm.password
        `$`("#input-password-confirmation").value = signupForm.passwordConfirmation
        `$`("#button-submit").click()
        return Selenide.page(T::class.java)
    }

    fun submitForm(username: String, password: String) = submitForm(SignupForm(username, password))

    fun submitForm(signupForm: SignupForm): HomePage {
        return submitForm<HomePage>(signupForm)
    }


}

class SignupForm(val username: String, val password: String, val passwordConfirmation: String = password)