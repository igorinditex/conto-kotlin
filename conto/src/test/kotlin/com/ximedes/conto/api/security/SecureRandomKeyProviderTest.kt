package com.ximedes.conto.api.security

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SecureRandomKeyProviderTest {

    @Test
    fun `it returns a copy of the key on every call to currentKey`() {
        val provider = SecureRandomKeyProvider()
        val key1 = provider.currentKey
        val key2 = provider.currentKey
        assertArrayEquals(key1, key2)
        assertNotSame(key1, key2)
    }
}