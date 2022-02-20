package com.ximedes.conto.integration.pages

import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.page

class CreateAccountPage : BasePage() {

    val errorMessages
        get() = `$`("#div-create-account-errors").text().split("\n")

    override fun isShowing() = `$`("#div-create-account-page").exists()

    fun createAccount(description: String?): HomePage {
        return createAccount<HomePage>(description)
    }

    inline fun <reified T : BasePage> createAccount(description: String?): T {
        `$`("#input-description").value = description
        `$`("#submit-create-account-form").click()
        assertPage<T>()
        return page(T::class.java)
    }


}