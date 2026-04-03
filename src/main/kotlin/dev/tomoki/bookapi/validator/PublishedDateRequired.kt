package dev.tomoki.bookapi.validator

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PublishedDateRequiredValidator::class])
annotation class PublishedDateRequired(
    val message: String = "出版済みの場合、publishedDate は必須です",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
