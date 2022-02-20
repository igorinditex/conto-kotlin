package com.ximedes.conto

import org.springframework.stereotype.Component
import java.text.Normalizer
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long) = this.fold(0L) { sum, it -> sum + selector(it) }

fun String.asCanonicalUsername() = Normalizer.normalize(this, Normalizer.Form.NFKC).toLowerCase(Locale.ROOT)

@Component("systemClock")
class SystemClock : Clock {
    override fun now(): Instant = Instant.now()
}

/**
 * A simple interface to provide an abstraction over using System.currentTimeMillis().
 *
 * The main purpose of using this interface is that it can be easily mocked in (unit) tests, allowing
 * for stable tests of time-dependent functionality.
 */
interface Clock {
    fun now(): Instant

    companion object {
        // The clock skew to use when comparing dates received from systems assumed to be in the local network
        // and therefore with more or less synchronized clocks
        val INTERNAL_CLOCK_SKEW: Duration = Duration.of(5, ChronoUnit.SECONDS)

        // The default clock skew to use when comparing dates received from external systems, whose
        // clocks are assumed to be potentially off by more than a few seconds
        val EXTERNAL_CLOCK_SKEW: Duration = Duration.of(5, ChronoUnit.MINUTES)
    }
}

