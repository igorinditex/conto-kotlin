package com.ximedes.conto.integration.pages

import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.`$`
import org.junit.jupiter.api.Assertions.assertTrue

abstract class BasePage {

    abstract fun isShowing(): Boolean

    fun navigationMenuShowing(): Boolean {
        return `$`("#div-navigation").exists()
    }

}

fun logoutFromUI(): LoginPage {
    `$`("#li-user-dropdown").click()
    `$`("#a-logout-link").click()
    return page()
}

inline fun <reified T : BasePage> assertPage(): T {
    val page = page<T>()
    assertTrue(page.isShowing())
    return page
}

inline fun <reified T : BasePage> open(location: String) = Selenide.open(location, T::class.java)

inline fun <reified T : BasePage> page() = Selenide.page(T::class.java)
