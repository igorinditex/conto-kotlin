package com.ximedes.conto.service

import com.ximedes.conto.db.TransferMapper
import com.ximedes.conto.domain.*
import com.ximedes.conto.domain.AccountNotAvailableException.Type.*
import com.ximedes.conto.sumByLong
import mu.KotlinLogging
import org.springframework.context.event.EventListener
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalStateException
import javax.security.auth.login.AccountNotFoundException

const val SIGNUP_BONUS = 100L

@Service
@Transactional
class TransferService(
    private val userService: UserService,
    private val accountService: AccountService,
    private val transferMapper: TransferMapper
) {

    private val logger = KotlinLogging.logger { }

    fun findBalance(accountID: String): Long {
        val account = accountService.findByAccountID(accountID)
            ?: throw IllegalArgumentException("No account found with ID $accountID")

        require(userService.loggedInUser.hasAccessTo(account)) {
            "User ${userService.loggedInUser} does not have access to account $accountID"
        }

        val accountBalance: Long
        // Check if the account balance is present in the DB
        if (account.balance != null) {
            accountBalance = account.balance
        } else {
            // If the balance is not present in the DB, the value has to be calculated.
            accountBalance = calculateBalanceThroughTransfersHistory(accountID)
            // Set the balance in the DB
            accountService.setAccountBalance(
                account.accountID,
                accountBalance
            )
        }
        return accountBalance
    }

    fun calculateBalanceThroughTransfersHistory(
        accountID: String
    ): Long {
        val transfers = transferMapper.findTransfersByAccountID(accountID)

        return transfers.sumByLong { t ->
            if (t.creditAccountID == t.debitAccountID) {
                // Corner case
                0L
            } else when (accountID) {
                t.creditAccountID -> t.amount
                t.debitAccountID -> -t.amount
                else -> throw IllegalStateException("Transfer $t should be in list of transfer for account $accountID")
            }
        }
    }

    // TODO this can go now, right? because these are runtimes?
    @Throws(InsufficientFundsException::class, AccountNotFoundException::class)
    @PreAuthorize("isAuthenticated()")
    fun attemptTransfer(
        debitAccountID: String,
        creditAccountID: String,
        amount: Long,
        description: String
    ): Transfer {
        val debitAccount = accountService.findByAccountID(debitAccountID)
            ?: throw AccountNotAvailableException(DEBIT, "Debit account with ID $debitAccountID not found.")
        val creditAccount = accountService.findByAccountID(creditAccountID)
            ?: throw AccountNotAvailableException(CREDIT, "Credit account with ID $creditAccountID not found.")

        require(userService.loggedInUser.hasAccessTo(debitAccount)) {
            "User ${userService.loggedInUser} does not have access to account $debitAccountID"
        }

        val debitAccountBalance: Long = debitAccount.balance ?: findBalance(debitAccount.accountID)

        if (debitAccountBalance - amount < debitAccount.minimumBalance) {
            throw InsufficientFundsException("Insufficient funds for transferring $amount from account ${debitAccount.accountID} with balance $debitAccountBalance")
        }

        // Update the balance of the debit account.
        accountService.updateAccountBalanceWhenTransfer(
            debitAccount.accountID, -amount
        )

        // Update the balance of the credit account.
        accountService.updateAccountBalanceWhenTransfer(
            creditAccount.accountID, amount
        )

        return Transfer(debitAccount.accountID, creditAccount.accountID, amount, description).also {
            transferMapper.insertTransfer(it)
        }

    }

    @PreAuthorize("isAuthenticated()")
    fun findTransfersByAccountID(accountID: String): List<Transfer> {
        val account = accountService.findByAccountID(accountID)
            ?: throw AccountNotAvailableException(UNKNOWN, "Account with ID $accountID not found.")

        require(userService.loggedInUser.hasAccessTo(account)) {
            "User ${userService.loggedInUser} does not have access to account $accountID"
        }
        return transferMapper.findTransfersByAccountID(accountID)
    }

    @EventListener
    fun onFirstAccountCreated(event: FirstAccountCreatedEvent) {
        logger.info { "Granting signup bonus to owner ${event.owner} of new first account ${event.accountID}" }
        val t = Transfer(accountService.rootAccount.accountID, event.accountID, SIGNUP_BONUS, "Welcome to Conto!")
        transferMapper.insertTransfer(t)
    }

}
