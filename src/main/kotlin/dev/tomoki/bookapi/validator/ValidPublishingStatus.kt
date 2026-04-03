package dev.tomoki.bookapi.validator

import dev.tomoki1.bookapi.validator.PublishingStatusConstraintValidator
import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PublishingStatusConstraintValidator::class])
annotation class ValidPublishingStatus(
    val message: String = "publishingStatus は ${PublishingStatusConstraintValidator.PUBLISHED} または ${PublishingStatusConstraintValidator.UNPUBLISHED} のみ有効です",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
