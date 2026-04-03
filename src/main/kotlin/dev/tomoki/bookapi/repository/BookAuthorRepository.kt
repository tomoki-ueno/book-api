package dev.tomoki.bookapi.repository

import dev.tomoki.bookapi.jooq.tables.records.BookAuthorRecord
import dev.tomoki.bookapi.jooq.tables.references.BOOK_AUTHOR
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class BookAuthorRepository(
    private val dsl: DSLContext
) {

    /**
     * 中間テーブル book_author に 1 件登録する。
     * returning() を使い、登録後のレコードを返す。
     */
    fun insert(record: BookAuthorRecord): BookAuthorRecord =
        dsl.insertInto(BOOK_AUTHOR)
            .set(record)
            .returning()
            .fetchOne()!!

    /**
     * 指定した book_id に紐づく著者情報をすべて削除する。
     */
    fun deleteByBookId(bookId: Long) {
        dsl.deleteFrom(BOOK_AUTHOR)
            .where(BOOK_AUTHOR.BOOK_ID.eq(bookId))
            .execute()
    }

    /**
     * 指定した book_id に紐づく著者ID一覧を取得する。
     * 必要に応じて Service 層で利用できるようにしておく。
     */
    fun findAuthorIdsByBookId(bookId: Long): List<Long> =
        dsl.select(BOOK_AUTHOR.AUTHOR_ID)
            .from(BOOK_AUTHOR)
            .where(BOOK_AUTHOR.BOOK_ID.eq(bookId))
            .fetch { it.value1() }
}
