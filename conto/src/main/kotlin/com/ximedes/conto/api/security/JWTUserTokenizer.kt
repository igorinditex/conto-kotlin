package com.ximedes.conto.api.security

import com.nimbusds.jose.JOSEException
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.ximedes.conto.Clock
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.text.ParseException
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

const val JWT_ISSUER = "CONTO FE API"

@Component
class JWTUserTokenizer(
        private val clock: Clock,
        private val keyProvider: KeyProvider,
        @Value("300") tokenTTLSeconds: Int
) {

    private val logger = KotlinLogging.logger { }
    private val tokenTTL = Duration.of(tokenTTLSeconds.toLong(), ChronoUnit.SECONDS)

    fun validateTokenAndExtractUsername(token: String?): String? {
        try {
            val validJWT = verifySignature(token) ?: return null
            val jwtClaimsSet = validJWT.jwtClaimsSet
            return if (validateClaimsSet(jwtClaimsSet)) {
                jwtClaimsSet.subject
            } else {
                null
            }
        } catch (e: ParseException) {
            logger.debug(e) { "Exception parsing token $token" }
            return null
        } catch (e: JOSEException) {
            logger.debug { "Exception parsing token $token" }
            return null
        }
    }

    fun createJWTForUsername(username: String?): String? {
        if (username == null) {
            return null
        }

        val now = clock.now()
        val expiryDate = now.plus(tokenTTL)

        // Prepare JWT with claims set
        val claimsSet = JWTClaimsSet.Builder()
            .subject(username)
            .issuer(JWT_ISSUER)
            .notBeforeTime(Date(now.toEpochMilli()))
            .expirationTime(Date(expiryDate.toEpochMilli()))
            .build()

        val header = JWSHeader(JWSAlgorithm.HS256)
        val signedJWT = SignedJWT(header, claimsSet)

        try {
            signedJWT.sign(MACSigner(keyProvider.currentKey))
        } catch (e: JOSEException) {
            logger.error(e) { "Error signing JWT token." }
            return null
        }

        return signedJWT.serialize()
    }

    /**
     * Parses a token and verifies the signature. Returns null
     * on failure, or a SignedJWT instance on success
     */
    private fun verifySignature(token: String?) = token?.let {
        val signedJWT = SignedJWT.parse(token)
        val verifier = MACVerifier(keyProvider.currentKey)

        if (signedJWT.verify(verifier)) signedJWT else null.also {
            logger.warn { "Received token with invalid signature. Token: '$token'" }
        }

    }

    private fun validateClaimsSet(claimsSet: JWTClaimsSet): Boolean {
        val now: Instant = clock.now()
        var valid = true

        val notBeforeDate = claimsSet.notBeforeTime
        if (notBeforeDate == null || now.isBefore(notBeforeDate.toInstant())) {
            logger.debug { "Received token with notBeforeDate of '$notBeforeDate' which is null or after the current time '$now' " }
            valid = false
        }

        val expirationDate = claimsSet.expirationTime
        if (expirationDate == null || now.isAfter(expirationDate.toInstant())) {
            logger.debug { "Received token with expirationDate of '$expirationDate' which is null or before the current time '$now' " }
            valid = false
        }

        val issuer = claimsSet.issuer
        if (issuer == null || issuer != JWT_ISSUER) {
            logger.debug { "Received token with invalid issuer '$issuer'." }
            valid = false
        }

        val username = claimsSet.subject
        if (username.isEmpty()) {
            logger.debug { "Received token with invalid subject '$username'" }
            valid = false
        }

        return valid
    }

}
