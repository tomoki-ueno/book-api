package dev.tomoki.bookapi.validator

import dev.tomoki.bookapi.dto.request.BookCreateRequest
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PublishedDateRequiredValidator : ConstraintValidator<PublishedDateRequired, BookCreateRequest> {

    override fun isValid(value: BookCreateRequest?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true

        // publishingStatus = PUBLISHED なら publishedDate 必須
        if (value.publishingStatus == "PUBLISHED" && value.publishedDate == null) {
            return false
        }

        return true
    }
}
