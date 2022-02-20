package com.ximedes.conto.web.validation

import com.ximedes.conto.web.controller.SignupForm
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import kotlin.reflect.KClass

/**
 * A class-level validation annotation that checks if two
 * supplied password fields are equal.
 *
 *
 * This is just the annotation definition. The actual validation is
 * done by [IdenticalPasswordsValidator]
 */
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = [IdenticalPasswordsValidator::class])
annotation class IdenticalPasswords(
    val message: String = "Password fields are not equal",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Any>> = []
)

/**
 * A validator that checks if two password fields are equal.
 *
 * It only works on instances of SignupForm, but since this class
 * is not designed for reuse in other applications that is ok.
 */
class IdenticalPasswordsValidator : ConstraintValidator<IdenticalPasswords, SignupForm> {
    override fun initialize(identicalPasswords: IdenticalPasswords) {
        // Nothing to do
    }

    override fun isValid(form: SignupForm, context: ConstraintValidatorContext): Boolean {
        return form.password == form.passwordConfirmation
    }
}