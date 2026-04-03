package dev.tomoki.bookapi.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.time.LocalDate

data class AuthorCreateRequest(
    @field:NotBlank(message = "著者名は必須です")
    val name: String?,

    @field:NotNull(message = "生年月日は必須です")
    @field:PastOrPresent(message = "生年月日は現在日以前である必要があります")
    val birthDate: LocalDate?
)
