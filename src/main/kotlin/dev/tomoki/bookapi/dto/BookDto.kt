package dev.tomoki.bookapi.dto

import java.time.LocalDate

data class BookDto(
    val bookId: Long?,
    val title: String?,
    val price: Int?,
    val publishingStatus: String?,
    val publishedDate: LocalDate?
)
