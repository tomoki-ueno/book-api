package dev.tomoki.bookapi.repository

import dev.tomoki.bookapi.jooq.tables.references.BOOK
import dev.tomoki.bookapi.jooq.tables.records.BookRecord
import dev.tomoki.bookapi.jooq.tables.references.BOOK_AUTHOR
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookRepository(
    private val dsl: DSLContext
) {
    fun insert(record: BookRecord): BookRecord =
        dsl.insertInto(BOOK)
            .set(record)
            .returning()
            .fetchOne()!!

    fun update(record: BookRecord): BookRecord =
        dsl.update(BOOK)
            .set(record)
            .where(BOOK.BOOK_ID.eq(record.bookId))
            .returning()
            .fetchOne()!!

    fun findById(id: Long): BookRecord? =
        dsl.selectFrom(BOOK)
            .where(BOOK.BOOK_ID.eq(id))
            .fetchOne()

    fun findAll(): List<BookRecord> =
        dsl.selectFrom(BOOK)
            .fetchInto(BookRecord::class.java)
    /**
     * 指定した著者に紐づく書籍一覧を取得する。
     *
     * book_author（中間テーブル）を JOIN して book を取得する。
     * 1:N の関係を想定しているため、複数件返る可能性がある。
     */
    fun findByAuthorId(authorId: Long): List<BookRecord> =
        dsl.select(BOOK.fields().toList())
            .from(BOOK)
            .join(BOOK_AUTHOR)
            .on(BOOK.BOOK_ID.eq(BOOK_AUTHOR.BOOK_ID))
            .where(BOOK_AUTHOR.AUTHOR_ID.eq(authorId))
            .fetchInto(BookRecord::class.java)
}
