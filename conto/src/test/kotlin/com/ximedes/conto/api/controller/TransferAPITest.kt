package com.ximedes.conto.api.controller

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.ximedes.conto.TransferBuilder
import com.ximedes.conto.TransferRequestBuilder
import com.ximedes.conto.service.TransferService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.springframework.validation.BindingResult


class TransferAPITest {
    val transferService = mock<TransferService>()
    val bindingResult = mock<BindingResult>()
    val api = TransferAPI(transferService)

    @Test
    fun `message is properly mapped to service call`() {
        val transferRequest = TransferRequestBuilder.build()
        val transfer = TransferBuilder.build { fromTransferRequest(transferRequest) }
        whenever(
            transferService.attemptTransfer(
                transferRequest.debitAccountID,
                transferRequest.creditAccountID,
                transferRequest.amount,
                transferRequest.description
            )
        ).thenReturn(transfer)

        val response = api.createTransfer(transferRequest, bindingResult)
        verify(transferService).attemptTransfer(
            transferRequest.debitAccountID,
            transferRequest.creditAccountID,
            transferRequest.amount,
            transferRequest.description
        )

        val transferDTO = (response.body as TransferResponse).transfer!!
        assertAll(
            { assertEquals(transfer.debitAccountID, transferDTO.debitAccountID) },
            { assertEquals(transfer.creditAccountID, transferDTO.creditAccountID) },
            { assertEquals(transfer.amount, transferDTO.amount) },
            { assertEquals(transfer.description, transferDTO.description) },
            { assertEquals(transfer.transferID, transferDTO.transferID) }
        )
    }

    @Test
    fun testTransferByAccountID() {
        val accountID = "123"
        val t = TransferBuilder.build { debitAccountID = accountID }
        whenever(transferService.findTransfersByAccountID(accountID)).thenReturn(listOf(t))

        val jt = api.getTransfersByAccountID(accountID).body?.single() ?: fail("Should not be null")

        assertAll(
            { assertEquals(t.debitAccountID, jt.debitAccountID) },
            { assertEquals(t.creditAccountID, jt.creditAccountID) },
            { assertEquals(t.amount, jt.amount) },
            { assertEquals(t.description, jt.description) },
            { assertEquals(t.transferID, jt.transferID) }
        )
    }


}