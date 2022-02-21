package com.ximedes.conto.service

import com.ximedes.conto.db.AccountMapper
import com.ximedes.conto.domain.*
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private const val FIRST_ACCOUNT_DESCRIPTION = "Checking"

@Service
@Transactional
class AccountService(private val accountMapper: AccountMapper,
                     private val userService: UserService,
                     private val eventPublisher: ApplicationEventPublisher) {

    private val logger = KotlinLogging.logger { }

    // This property will be set when an admin user is created,
    // which should happen on startup
    lateinit var rootAccount: Account

    @EventListener
    fun onAdminUserCreated(event: AdminUserCreatedEvent) {
        rootAccount = doCreateAccount(event.adminUsername, "Bank", Long.MIN_VALUE)
    }

    @EventListener
    fun onUserSignedUp(event: UserSignedUpEvent) {
        val a = doCreateAccount(event.username, FIRST_ACCOUNT_DESCRIPTION, 0L)
        eventPublisher.publishEvent(FirstAccountCreatedEvent(this, a.owner, a.accountID))
    }

    fun findByAccountID(accountID: String?) = accountMapper.find(AccountCriteria(null, accountID)).firstOrNull()

    fun findAllAccounts(): List<Account> = accountMapper.find(AccountCriteria())

    fun setAccountBalance(accountId: String, balance: Long) = accountMapper.setAccountBalance(accountId, balance)

    fun updateAccountBalanceWhenTransfer(accountId: String, amount: Long) = accountMapper.updateAccountBalanceWhenTransfer(accountId, amount)

    @PreAuthorize("hasRole('ROLE_USER')")
    fun createAccount(description: String): Account {
        val username = userService.loggedInUser!!.username
        return doCreateAccount(username, description, 0L)
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun createAccount(ownerName: String, description: String, minimumBalance: Long): Account {
        // Make sure the user exists before creating the account
        val owner = userService.findByUsername(ownerName) ?: throw UsernameNotFoundException(ownerName)
        return doCreateAccount(owner.username, description, minimumBalance)
    }

    private fun doCreateAccount(owner: String, description: String, minimumBalance: Long): Account {
        val accountID = generateAccountID()
        // By default create a Account with the balance field set to null.
        val account = Account(accountID, owner, description, minimumBalance, null)
        accountMapper.insertAccount(account)
        logger.info("Created new account $account.")
        return account
    }


    private fun generateAccountID(): String {
        val numericID = accountMapper.getNextUniqueID()
        val accountIDUniquePart = numericID.toString().padStart(8, '0')
        return "NLBRAT$accountIDUniquePart"
    }

    fun findByOwner(user: String) = accountMapper.find(AccountCriteria(ownerID = user))


}