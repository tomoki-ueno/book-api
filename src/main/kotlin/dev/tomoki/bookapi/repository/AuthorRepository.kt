package dev.tomoki.bookapi.repository

import dev.tomoki.bookapi.jooq.tables.records.AuthorRecord
import dev.tomoki.bookapi.jooq.tables.references.AUTHOR
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AuthorRepository(
    private val dsl: DSLContext
) {

    /**
     * 著者を登録する。
     * returning() を使い、登録後の author_id を含むレコードを返す。
     */
    fun insert(record: AuthorRecord): AuthorRecord =
        dsl.insertInto(AUTHOR)
            .set(record)
            .returning()
            .fetchOne()!!

    /**
     * 著者を更新する。
     * returning() を使い、登録後の author_id を含むレコードを返す。
     */
    fun update(record: AuthorRecord): AuthorRecord =
        dsl.update(AUTHOR)
            .set(record)
            .where(AUTHOR.AUTHOR_ID.eq(record.authorId))
            .returning()
            .fetchOne()!!

    /**
     * 著者を ID で取得する。
     * 見つからない場合は null を返す。
     */
    fun findById(id: Long): AuthorRecord? =
        dsl.selectFrom(AUTHOR)
            .where(AUTHOR.AUTHOR_ID.eq(id))
            .fetchOne()

}
