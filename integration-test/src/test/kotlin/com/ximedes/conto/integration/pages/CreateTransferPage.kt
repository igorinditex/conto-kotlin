package com.ximedes.conto.integration.pages

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Selenide.`$`

class CreateTransferPage : BasePage() {
    override fun isShowing() = `$`("#div-transfer-page").exists()

    fun transfer(form: TransferForm): HomePage {
        return transfer<HomePage>(form)
    }

    inline fun <reified T : BasePage> transfer(form: TransferForm): T {
        selectDebitAccount(form)
        selectCreditAccount(form)
        `$`("#input-description").value = form.description
        `$`("#input-amount").value = form.amount.toString()
        `$`("#button-submit-transfer-form").click()
        return assertPage()
    }

    fun selectDebitAccount(form: TransferForm) {
        if (form.debitAccountID != null) {
            `$`("#select-from-account").selectOptionByValue(form.debitAccountID)
        } else if (form.debitAccountDescription != null) {
            `$`("#select-from-account").selectOptionContainingText(form.debitAccountDescription)
        }
    }

    fun selectCreditAccount(form: TransferForm) {
        if (form.creditAccountID != null) {
            `$`("#button-find-account").click()
            `$`("#div-find-account-modal").should(Condition.visible)
            `$`("#account-table_filter").`$`("input").value = form.creditAccountID
            `$`("#div-find-account-modal").`$$`("a").find(Condition.matchesText(form.creditAccountID))
                .click()
            `$`("#div-find-account-modal").shouldNotBe(Condition.visible)
        } else if (form.creditAccountDescription != null) {
            `$`("#button-find-account").click()
            `$`("#div-find-account-modal").should(Condition.visible)
            `$`("#account-table_filter").`$`("input").value = form.creditAccountDescription
            `$`("#div-find-account-modal").`$$`("td")
                .find(Condition.matchesText(form.creditAccountDescription)).parent().`$`("a").click()
            `$`("#div-find-account-modal").shouldNotBe(Condition.visible)
        }
    }

}

class TransferForm(
    val debitAccountDescription: String? = null,
    val debitAccountID: String? = null,
    val creditAccountDescription: String? = null,
    val creditAccountID: String? = null,
    val amount: Long,
    val description: String
)