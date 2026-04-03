package dev.tomoki.bookapi.dto.request

import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class AuthorUpdateRequest(
    @field:Size(min = 1, message = "著者名は空にできません")
    val name: String?,

    @field:PastOrPresent(message = "生年月日は現在日以前である必要があります")
    val birthDate: LocalDate?
)
