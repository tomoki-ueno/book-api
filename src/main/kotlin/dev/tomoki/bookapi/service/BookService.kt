package dev.tomoki.bookapi.service

import dev.tomoki.bookapi.dto.request.BookCreateRequest
import dev.tomoki.bookapi.dto.request.BookUpdateRequest
import dev.tomoki.bookapi.dto.response.BookResponse
import dev.tomoki.bookapi.exception.NotFoundException
import dev.tomoki.bookapi.jooq.tables.records.BookAuthorRecord
import dev.tomoki.bookapi.jooq.tables.records.BookRecord
import dev.tomoki.bookapi.repository.AuthorRepository
import dev.tomoki.bookapi.repository.BookAuthorRepository
import dev.tomoki.bookapi.repository.BookRepository
import dev.tomoki1.bookapi.validator.PublishingStatusConstraintValidator
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val bookAuthorRepository: BookAuthorRepository,
    private val dsl: DSLContext
) {
    /**
     * 書籍を登録する。
     * returning() により book_id を含むレコードが返る。
     * 1. book を insert
     * 2. book_author に著者を紐づける
     */
    @Transactional
    fun createBook(req: BookCreateRequest): BookResponse {
        // 著者が存在するかチェック
        req.authorIds?.forEach {
            authorRepository.findById(it)
                ?: throw NotFoundException("Author not found: $it")
        }

        // book を insert
        val bookRecord = BookRecord().apply {
            title = req.title
            price = req.price
            publishingStatus = req.publishingStatus
            publishedDate = req.publishedDate
        }
        val insertedBook = bookRepository.insert(bookRecord)

        // book_author に著者を紐づける
        req.authorIds?.forEach { authorId ->
            val ba = BookAuthorRecord().apply {
                bookId = insertedBook.bookId
                this.authorId = authorId
            }
            bookAuthorRepository.insert(ba)
        }

        return toResponse(
            insertedBook,
            bookAuthorRepository.findAuthorIdsByBookId(insertedBook.bookId!!)
        )
    }

    /**
     * 書籍を更新する。
     * 著者の紐づけは一度削除してから再登録する（全置換）。
     */
    @Transactional
    fun updateBook(bookId: Long, req: BookUpdateRequest): BookResponse {
        val existing = bookRepository.findById(bookId)
            ?: throw NotFoundException("Book not found: $bookId")

        if (req.publishingStatus != null) {
            if (existing.publishingStatus == PublishingStatusConstraintValidator.PUBLISHED &&
                req.publishingStatus == PublishingStatusConstraintValidator.UNPUBLISHED) {
                throw IllegalArgumentException("出版済みの書籍を未出版に戻すことはできません")
            }
        }

        // null の項目は更新しない
        val updated = listOfNotNull(
            req.title?.also { existing.title = it },
            req.price?.also { existing.price = it },
            req.publishingStatus?.also { existing.publishingStatus = it },
            req.publishedDate?.also { existing.publishedDate = it }
        ).let { changes ->
            if (changes.isEmpty()) existing else bookRepository.update(existing)
        }

        if (updated.publishingStatus == "PUBLISHED" && updated.publishedDate == null) {
            throw IllegalArgumentException("出版済みに更新する場合、publishedDate は必須です")
        }

        // 著者の紐づけを更新
        req.authorIds?.let { authorIds ->
            // 一度削除
            bookAuthorRepository.deleteByBookId(bookId)

            // 再登録
            authorIds.forEach { authorId ->
                val ba = BookAuthorRecord().apply {
                    this.bookId = bookId
                    this.authorId = authorId
                }
                bookAuthorRepository.insert(ba)
            }
        }

        return toResponse(
            updated,
            bookAuthorRepository.findAuthorIdsByBookId(bookId)
        )
    }

    /**
     * 著者に紐づく書籍一覧を取得する。
     */
    fun findBooksByAuthor(authorId: Long): List<BookResponse> {
        // 著者が存在するかチェック
        authorRepository.findById(authorId)
            ?: throw NotFoundException("Author not found: $authorId")

        return bookRepository.findByAuthorId(authorId)
            .map { record ->
                toResponse(
                    record,
                    bookAuthorRepository.findAuthorIdsByBookId(record.bookId!!)
                )
            }
    }

    private fun toResponse(record: BookRecord,authorIds: List<Long>) = BookResponse(
        bookId = record.bookId!!,
        title = record.title!!,
        price = record.price!!,
        publishingStatus = record.publishingStatus!!,
        publishedDate = record.publishedDate,
        authorIds = authorIds
    )
}
