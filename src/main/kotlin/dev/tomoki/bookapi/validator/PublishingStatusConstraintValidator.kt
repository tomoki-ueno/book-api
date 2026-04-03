package dev.tomoki1.bookapi.validator

import dev.tomoki.bookapi.validator.ValidPublishingStatus
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PublishingStatusConstraintValidator :
    ConstraintValidator<ValidPublishingStatus, String?> {

    companion object {
        const val PUBLISHED = "PUBLISHED"
        const val UNPUBLISHED = "UNPUBLISHED"

        val VALID_STATUSES = setOf(PUBLISHED, UNPUBLISHED)
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true
        return value in VALID_STATUSES
    }

}

