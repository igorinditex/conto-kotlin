package com.ximedes.conto.service

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ximedes.conto.AccountBuilder
import com.ximedes.conto.TransferBuilder
import com.ximedes.conto.UserBuilder
import com.ximedes.conto.db.TransferMapper
import com.ximedes.conto.domain.*
import com.ximedes.conto.domain.AccountNotAvailableException.Type.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.never
import java.lang.IllegalStateException

class TransferServiceTest {

    val userService = mock<UserService>()
    val accountService = mock<AccountService>()
    val transferMapper = mock<TransferMapper>()

    val transferService = TransferService(userService, accountService, transferMapper)
    val transferCaptor = argumentCaptor<Transfer>()

    @Test
    fun `transfer from a user's own account with sufficient balance succeed, fail with insufficient balance`() {
        val user = UserBuilder.build()
        whenever(userService.loggedInUser).thenReturn(user)
        whenever(userService.findByUsername(user.username)).thenReturn(user)

        val debitAccount = AccountBuilder.build {
            owner = user.username
        }
        val creditAccount = AccountBuilder.build()
        whenever(accountService.findByAccountID(debitAccount.accountID)).thenReturn(debitAccount)
        whenever(accountService.findByAccountID(creditAccount.accountID)).thenReturn(creditAccount)

        // Balance is 150, so should succeed
        whenever(transferMapper.findTransfersByAccountID(debitAccount.accountID)).thenReturn(listOf(TransferBuilder.build {
            creditAccountID = debitAccount.accountID
            amount = 150
        }))

        val t = transferService.attemptTransfer(debitAccount.accountID, creditAccount.accountID, 100, "desc")
        verify(transferMapper).insertTransfer(t)

        assertEquals(debitAccount.accountID, t.debitAccountID)
        assertEquals(creditAccount.accountID, t.creditAccountID)
        assertEquals(100, t.amount)
        assertEquals("desc", t.description)

        // Balance is 50, so should fail
        whenever(transferMapper.findTransfersByAccountID(debitAccount.accountID)).thenReturn(listOf(TransferBuilder.build {
            creditAccountID = debitAccountID
            amount = 50
        }))

        assertThrows<InsufficientFundsException> {
            transferService.attemptTransfer(debitAccount.accountID, creditAccount.accountID, 100, "desc")
        }
    }

    @Test
    fun `a regular user may not transfer money from accounts not owned by them`() {
        val user = UserBuilder.build {
            username = "someone"
        }
        val debitAccount = AccountBuilder.build {
            owner = "someone_else"
        }

        val creditAccount = AccountBuilder.build()

        whenever(userService.loggedInUser).thenReturn(user)
        whenever(userService.findByUsername(user.username)).thenReturn(user)
        whenever(accountService.findByAccountID(debitAccount.accountID)).thenReturn(debitAccount)
        whenever(accountService.findByAccountID(creditAccount.accountID)).thenReturn(creditAccount)

        assertThrows<IllegalArgumentException> {
            transferService.attemptTransfer(debitAccount.accountID, creditAccount.accountID, 1, "desc")
        }
    }

    @Test
    fun `an admin user may transfer money from an account not owned by them`() {
        val admin = UserBuilder.build {
            username = "admin"
            role = Role.ADMIN
        }

        val debitAccount = AccountBuilder.build {
            owner = "not_admin"
        }

        val creditAccount = AccountBuilder.build()

        whenever(userService.loggedInUser).thenReturn(admin)
        whenever(userService.findByUsername(admin.username)).thenReturn(admin)
        whenever(accountService.findByAccountID(debitAccount.accountID)).thenReturn(debitAccount)
        whenever(accountService.findByAccountID(creditAccount.accountID)).thenReturn(creditAccount)

        // Balance is 50 on debit account
        whenever(transferMapper.findTransfersByAccountID(debitAccount.accountID)).thenReturn(listOf(TransferBuilder.build {
            creditAccountID = debitAccount.accountID
            amount = 50
        }))

        val t = transferService.attemptTransfer(debitAccount.accountID, creditAccount.accountID, 10, "desc")
        verify(transferMapper).insertTransfer(t)

        assertEquals(debitAccount.accountID, t.debitAccountID)
        assertEquals(creditAccount.accountID, t.creditAccountID)
        assertEquals(10, t.amount)
        assertEquals("desc", t.description)
    }


    @Test
    fun `findTransfersByAccountID finds transfers by account ID`() {
        val user = UserBuilder.build()
        whenever(userService.loggedInUser).thenReturn(user)
        val account = AccountBuilder.build {
            owner = user.username
        }

        val l = listOf(TransferBuilder.build {
            debitAccountID = account.accountID
        })
        whenever(accountService.findByAccountID(account.accountID)).thenReturn(account)
        whenever(transferMapper.findTransfersByAccountID(account.accountID)).thenReturn(l)
        val t = transferService.findTransfersByAccountID(account.accountID)
        assertSame(t, l)
    }

    @Test
    fun `only owners and admins can get the list of transfer for an account`() {
        val userA = UserBuilder.build()
        val account = AccountBuilder.build {
            owner = userA.username
        }
        whenever(accountService.findByAccountID(account.accountID)).thenReturn(account)

        val userB = UserBuilder.build()
        whenever(userService.loggedInUser).thenReturn(userB)
        assertThrows<IllegalArgumentException> {
            transferService.findTransfersByAccountID(account.accountID)
        }

        val admin = UserBuilder.build {
            username = "admin"
            role = Role.ADMIN
        }
        whenever(userService.loggedInUser).thenReturn(admin)
        assertTrue(transferService.findTransfersByAccountID(account.accountID).isEmpty())
    }

    @Test
    fun `it knows how to calculate balance from credit and debit transactions`() {
        val user = UserBuilder.build()
        whenever(userService.loggedInUser).thenReturn(user)

        val (accountA, accountB, accountC) = AccountBuilder.build(3) {
            owner = user.username
        }
        whenever(accountService.findByAccountID(accountA.accountID)).thenReturn(accountA)
        whenever(accountService.findByAccountID(accountB.accountID)).thenReturn(accountB)
        whenever(accountService.findByAccountID(accountC.accountID)).thenReturn(accountC)

        val txAtoB = (1..100).map {
            TransferBuilder.build {
                debitAccountID = accountA.accountID
                creditAccountID = accountB.accountID
                amount = 3
            }
        }
        val txBtoC = (1..75).map {
            TransferBuilder.build {
                debitAccountID = accountB.accountID
                creditAccountID = accountC.accountID
                amount = 1
            }
        }
        whenever(transferMapper.findTransfersByAccountID(accountA.accountID)).thenReturn(txAtoB)
        whenever(transferMapper.findTransfersByAccountID(accountB.accountID)).thenReturn(txAtoB + txBtoC)
        whenever(transferMapper.findTransfersByAccountID(accountC.accountID)).thenReturn(txBtoC)

        assertEquals(-300, transferService.findBalance(accountA.accountID))
        assertEquals(225, transferService.findBalance(accountB.accountID))
        assertEquals(75, transferService.findBalance(accountC.accountID))
    }

    @Test
    fun `on receiving a FirstAccountCreatedEvent the account is credited with the signup bonus from the root account`() {
        whenever(accountService.rootAccount).thenReturn(AccountBuilder.build { accountID = "root" })
        transferService.onFirstAccountCreated(FirstAccountCreatedEvent(this, "owner", "account"))
        verify(transferMapper).insertTransfer(transferCaptor.capture())
        val t = transferCaptor.firstValue
        assertEquals(SIGNUP_BONUS, t.amount)
        assertEquals("root", t.debitAccountID)
        assertEquals("account", t.creditAccountID)
    }


    @Test
    fun `it throws an exception when using a non-existing account`() {
        whenever(accountService.findByAccountID("foo")).thenReturn(null)
        whenever(accountService.findByAccountID("bar")).thenReturn(AccountBuilder.build { accountID = "bar" })
        assertThrows<IllegalArgumentException> {
            transferService.findBalance("foo")
        }

        assertThrows<AccountNotAvailableException> {
            transferService.attemptTransfer("foo", "bar", 1, "desc")
        }.also { assertEquals(DEBIT, it.type) }

        assertThrows<AccountNotAvailableException> {
            transferService.attemptTransfer("bar", "foo", 1, "desc")
        }.also { assertEquals(CREDIT, it.type) }

        assertThrows<AccountNotAvailableException> {
            transferService.findTransfersByAccountID("foo")
        }.also { assertEquals(UNKNOWN, it.type) }
    }

    @Test
    fun `it throws an exception when finding the balance of an account of which the current user is not the owner or the admin`() {
        whenever(accountService.findByAccountID("foo")).thenReturn(AccountBuilder.build {
            owner = "owner"
        })
        whenever(userService.loggedInUser).thenReturn(UserBuilder.build {
            username = "notowner"
        })
        assertThrows<IllegalArgumentException> {
            transferService.findBalance("foo")
        }
    }

    @Test
    fun `findByAccountID finds account with balance by account ID and avoid calculate balance`() {
        val user = UserBuilder.build()

        whenever(userService.loggedInUser).thenReturn(user)

        val account = AccountBuilder.build {
            owner = user.username
            accountID = "NLBRAT00075566"
            balance = 150L
        }

        whenever(accountService.findByAccountID(account.accountID)).thenReturn(account)
        assertEquals(150L, transferService.findBalance(account.accountID))
        verify(transferMapper, never()).findTransfersByAccountID(account.accountID)
    }

    @Test
    fun `it throws an exception when transfer is not in list of transfer for account`() {

        val accountID = "NLBRAT00075566"

        whenever(transferMapper.findTransfersByAccountID(accountID)).thenReturn(listOf(TransferBuilder.build {
        }))

        assertThrows<IllegalStateException> {
            transferService.calculateBalanceThroughTransfersHistory(accountID)
        }
    }

}