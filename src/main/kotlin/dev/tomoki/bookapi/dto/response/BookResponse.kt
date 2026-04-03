package dev.tomoki.bookapi.dto.response

import java.time.LocalDate

data class BookResponse(
    val bookId: Long?,
    val title: String?,
    val price: Int?,
    val publishingStatus: String?,
    val publishedDate: LocalDate?,
    val authorIds: List<Long>
)
