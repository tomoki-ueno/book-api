package dev.tomoki.bookapi.dto.response

import java.time.LocalDate

data class AuthorResponse(
    val authorId: Long,
    val name: String,
    val birthDate: LocalDate
)
