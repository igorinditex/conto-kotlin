package com.ximedes.conto.web.controller

import com.ximedes.conto.web.validation.IdenticalPasswords
import com.ximedes.conto.web.validation.NotCommonPassword
import org.hibernate.validator.constraints.CodePointLength
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

open class CreateAccountForm(
    @get:NotBlank(message = "Description is a required field")
    @get:CodePointLength(max = 64, message = "Description may not be larger than 64 characters")
    @get:Pattern(
        regexp = "[\\p{L}|\\p{M}|\\p{N}|\\p{P}|\\p{Zs}]*",
        message = "Description may only contain letters, marks, numbers, punctuation and spaces"
    )
    var description: String? = null
)

class AdminCreateAccountForm(
    // We don't do a lot of validation here, because if the owner ID isn't
    // an existing username the account creation will fail regardless
    @get:NotNull
    @get:CodePointLength(min = 1)
    var ownerID: String? = null,
    var minimumBalance: Long? = null,
    description: String? = null
) : CreateAccountForm(description)

class TransferForm(
    @get:NotNull
    @get:CodePointLength(min = 1, max = 40)
    @get:Pattern(regexp = "[\\p{L}|\\p{N}-]*", message = "AccountID may only contain letters, numbers, and dashes")
    var fromAccountID: String? = null,

    @get:NotNull
    @get:CodePointLength(min = 1, max = 40)
    @get:Pattern(regexp = "[\\p{L}|\\p{N}-]*", message = "AccountID may only contain letters, numbers, and dashes")
    var toAccountID: String? = null,

    @get:Min(1)
    var amount: Long? = null,

    @get:NotNull
    @get:CodePointLength(min = 1, max = 512)
    @get:Pattern(
        regexp = "[\\p{L}|\\p{M}|\\p{N}|\\p{P}|\\p{Zs}]*",
        message = "Description may only contain letters, marks, numbers, punctuation and spaces"
    )
    var description: String? = null
)

@IdenticalPasswords
class SignupForm(
    @get:NotNull(message = "Username is required")
    @get:CodePointLength(
        min = 3,
        max = 20,
        message = "Username must be between 3 and 20 characters"
    )
    // Only letters and numbers - see http://www.regular-expressions.info/unicode.html
    @get:Pattern(regexp = "[\\p{L}|\\p{M}|\\p{N}]*", message = "Username may only contain letters, marks and numbers")
    var username: String? = null,

    // Password requirements chosen according to NIST guidance - https://pages.nist.gov/800-63-3/sp800-63b.html#sec5
    @get:NotNull(message = "Password is required")
    @get:CodePointLength(
        min = 8,
        max = 4096,
        message = "Password must be between 8 and 4096 characters"
    )
    // Only letters, numbers and punctuation - see http://www.regular-expressions.info/unicode.html
    @get:Pattern(
        regexp = "[\\p{L}|\\p{M}|\\p{N}|\\p{P}|\\p{Zs}]*",
        message = "Password may only contain letters, marks, numbers, punctuation and spaces"
    )
    @get:NotCommonPassword
    var password: String? = null,
    var passwordConfirmation: String? = null
)