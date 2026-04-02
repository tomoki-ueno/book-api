package dev.tomoki.bookapi.repository

import dev.tomoki.bookapi.jooq.tables.references.BOOK
import dev.tomoki.bookapi.jooq.tables.records.BookRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookRepository(
    private val dsl: DSLContext
) {
    fun findAll(): List<BookRecord> =
        dsl.selectFrom(BOOK).fetch()
}
