package com.ximedes.conto.api.security

import org.springframework.stereotype.Component
import java.security.SecureRandom

interface KeyProvider {
    val currentKey: ByteArray
}

private const val KEY_LENGTH_BYTES = 256

@Component
class SecureRandomKeyProvider : KeyProvider {

    private val _currentKey = ByteArray(KEY_LENGTH_BYTES).also {
        SecureRandom().nextBytes(it)
    }

    override val currentKey
        get() = _currentKey.copyOf(KEY_LENGTH_BYTES)

}


