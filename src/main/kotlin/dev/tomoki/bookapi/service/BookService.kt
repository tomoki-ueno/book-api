package dev.tomoki.bookapi.service

import dev.tomoki.bookapi.dto.BookDto
import dev.tomoki.bookapi.jooq.tables.records.BookRecord
import dev.tomoki.bookapi.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository
) {
    fun findAll(): List<BookDto> =
        bookRepository.findAll().map { record ->
            BookDto(
                bookId = record.bookId,
                title = record.title,
                price = record.price,
                publishingStatus = record.publishingStatus,
                publishedDate = record.publishedDate
            )
        }
}
