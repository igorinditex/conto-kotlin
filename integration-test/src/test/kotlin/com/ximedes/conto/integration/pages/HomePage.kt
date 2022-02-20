package com.ximedes.conto.integration.pages

import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.SelenideElement
import org.openqa.selenium.support.FindBy

class HomePage : BasePage() {

    @FindBy(id = "a-create-account")
    lateinit var createAccountLink: SelenideElement

    @FindBy(id = "a-create-transfer")
    lateinit var createTransferLink: SelenideElement

    override fun isShowing() = `$`("#div-page-home").exists()

    fun accountRows(): List<AccountRow> = `$`("#div-account-list").`$$`("tbody tr").map { tr ->
        val td = tr.`$$`("td").iterator()
        val accountID = td.next().text()
        val description = td.next().text()
        val minimumBalance = td.next().text()
        val balance = td.next().text()
        AccountRow(accountID, description, minimumBalance, balance)
    }

    fun transferRows(): List<TransferRow> {
        return transferRows("")
    }

    fun transferRows(searchString: String?): List<TransferRow> {
        `$`("#transfer-table_filter").`$`("input").value = searchString
        return `$`("#div-transfer-list").`$$`("tbody tr").map { tr ->
            val td: Iterator<SelenideElement> = tr.`$$`("td").iterator()
            val transferID = td.next().text()
            val fromTo = td.next().text()
            val otherAccountID = td.next().text()
            val description = td.next().text()
            val amount = td.next().text()
            TransferRow(transferID, fromTo, otherAccountID, description, amount)
        }
    }

    fun clickCreateAccountButton(): CreateAccountPage {
        createAccountLink.click()
        return assertPage()
    }

    fun findAccountIDsForDescription(description: String?): List<String> {
        return accountRows().filter { it.description == description }.map { it.accountID }
    }

    fun clickCreateTransferButton(): CreateTransferPage {
        createTransferLink.click()
        return assertPage()
    }

}

data class TransferRow(
    val transferID: String,
    val fromTo: String,
    val otherAccountID: String,
    val description: String,
    val amount: Long
) {
    constructor(transferID: String, fromTo: String, otherAccountID: String, description: String, amount: String) : this(
        transferID,
        fromTo,
        otherAccountID,
        description,
        amount.toLong()
    )
}

data class AccountRow(val accountID: String, val description: String, val minimumBalance: Long, val balance: Long) {
    constructor(accountID: String, description: String, minimumBalance: String, balance: String) : this(
        accountID,
        description,
        minimumBalance.toLong(),
        balance.toLong()
    )
}