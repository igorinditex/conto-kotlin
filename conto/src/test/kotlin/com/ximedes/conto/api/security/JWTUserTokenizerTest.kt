package com.ximedes.conto.api.security

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.ximedes.conto.Clock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.time.temporal.ChronoUnit.SECONDS
import java.util.*


class JWTUserTokenizerTest {

    val staticClock = StaticClock()
    val ttl = 123
    val username = "jwt-username"
    val keyProvider = SecureRandomKeyProvider()
    val tokenizer = JWTUserTokenizer(staticClock, keyProvider, ttl)

    fun buildJWT(keyProvider: KeyProvider = this.keyProvider, config: JWTBuilder.() -> Unit = {}): String {
        return JWTBuilder(keyProvider).apply(config).build()
    }

    @Test
    fun `it accepts tokens it has generated itself`() {
        val token = tokenizer.createJWTForUsername(username)
        val usernameFromToken = tokenizer.validateTokenAndExtractUsername(token)
        assertEquals(username, usernameFromToken)
    }

    @Test
    fun `it works with an externally generated valid token`() {
        val token = buildJWT()
        assertEquals(username, tokenizer.validateTokenAndExtractUsername(token))
    }

    @Test
    fun `it rejects tokens with an empty subject`() {
        val token = buildJWT {
            subject = ""
        }
        assertNull(tokenizer.validateTokenAndExtractUsername(token))
    }

    @Test
    fun `it handles null input`() {
        assertNull(tokenizer.createJWTForUsername(null))
        assertNull(tokenizer.validateTokenAndExtractUsername(null))
    }

    @Test
    fun `it rejects tokens with invalid signature`() {
        // Create a new random key so the signatures won't match
        val token = buildJWT(SecureRandomKeyProvider())
        assertNull(tokenizer.validateTokenAndExtractUsername(token))
    }

    @Test
    fun `it rejects tokens presented before the NotBeforeTime`() {
        val token = buildJWT {
            notBeforeTime = staticClock.now().plus(1, SECONDS)
        }
        assertNull(tokenizer.validateTokenAndExtractUsername(token))
    }

    @Test
    fun `it rejects tokens presented after the expiration time`() {
        val token = buildJWT {
            expirationTime = staticClock.now().minus(1, SECONDS)
        }
        assertNull(tokenizer.validateTokenAndExtractUsername(token))
    }

    @Test
    fun `it rejects token with an invalid issuer`() {
        val token = buildJWT {
            issuer = "some other isser"
        }
        assertNull(tokenizer.validateTokenAndExtractUsername(token))
    }

    @Test
    fun `it returns null on non-JWT input`() {
        assertNull(tokenizer.validateTokenAndExtractUsername("this is not a JWT"))
    }

    @Test
    fun `signing with an invalid key returns null`() {
        val tokenizer = JWTUserTokenizer(staticClock, object : KeyProvider {
            override val currentKey = ByteArray(0)
        }, ttl)
        assertNull(tokenizer.createJWTForUsername(username))
    }


    inner class JWTBuilder(private val keyProvider: KeyProvider) {
        var subject = username
        var issuer = JWT_ISSUER
        var issueTime = staticClock.now()
        var notBeforeTime = issueTime.minus(1, ChronoUnit.HOURS)
        var expirationTime = issueTime.plus(5, ChronoUnit.MINUTES)

        private fun Instant.asDate() = Date(this.toEpochMilli())

        fun build(): String {
            val claims = JWTClaimsSet.Builder().subject(subject).issuer(issuer).issueTime(issueTime.asDate()).notBeforeTime(notBeforeTime.asDate()).expirationTime(expirationTime.asDate()).build()
            val header = JWSHeader(JWSAlgorithm.HS256)
            val signedJWT = SignedJWT(header, claims)
            try {
                val signer = MACSigner(keyProvider.currentKey)
                signedJWT.sign(signer)
            } catch (e: JOSEException) {
                throw RuntimeException(e)
            }
            return signedJWT.serialize()
        }

    }

    class StaticClock : Clock {
        private val now = Instant.now()

        override fun now(): Instant = now
    }


}


