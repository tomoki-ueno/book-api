package dev.tomoki.bookapi.dto

import java.util.*

data class BookDto(
    val book_id: Long,
    val title: String,
    var price: Int,
    val publishing_status: String,
    val published_date: Date
)
