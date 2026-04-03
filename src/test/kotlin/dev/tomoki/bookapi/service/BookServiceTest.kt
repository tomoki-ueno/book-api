package dev.tomoki.bookapi.service

import dev.tomoki.bookapi.dto.request.BookCreateRequest
import dev.tomoki.bookapi.dto.request.BookUpdateRequest
import dev.tomoki.bookapi.exception.NotFoundException
import dev.tomoki.bookapi.jooq.tables.records.AuthorRecord
import dev.tomoki.bookapi.jooq.tables.records.BookRecord
import dev.tomoki.bookapi.repository.AuthorRepository
import dev.tomoki.bookapi.repository.BookAuthorRepository
import dev.tomoki.bookapi.repository.BookRepository
import org.jooq.DSLContext
import org.mockito.kotlin.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BookServiceTest {

    private val bookRepository = mock<BookRepository>()
    private val authorRepository = mock<AuthorRepository>()
    private val bookAuthorRepository = mock<BookAuthorRepository>()
    private val dsl = mock<DSLContext>()
    private val bookService = BookService(bookRepository, authorRepository, bookAuthorRepository, dsl)
    private val authorService = mock<AuthorService>()

    // -----------------------------
    // 正常系
    // -----------------------------

    @Test
    fun `createBook - authorIds が複数でも全員存在すれば成功`() {
        whenever(authorRepository.findById(1L)).thenReturn(
            AuthorRecord().apply { authorId = 1; name = "A" }
        )

        whenever(authorRepository.findById(2L)).thenReturn(
            AuthorRecord().apply { authorId = 2; name = "B" }
        )

        val req = BookCreateRequest(
            title = "本",
            price = 1000,
            publishingStatus = "UNPUBLISHED",
            publishedDate = null,
            authorIds = listOf(1L, 2L)
        )

        val inserted = BookRecord().apply {
            bookId = 1
            title = "本"
            price = 1000
            publishingStatus = "UNPUBLISHED"
        }

        whenever(bookRepository.insert(any())).thenReturn(inserted)

        val result = bookService.createBook(req)

        assertEquals(1, result.bookId)
    }

    @Test
    fun `updateBook - 未出版から出版済みへ変更は成功`() {
        val existing = BookRecord().apply {
            bookId = 1
            title = "本"
            price = 1000
            publishedDate = LocalDate.now()
            publishingStatus = "UNPUBLISHED"
        }

        whenever(bookRepository.findById(1L)).thenReturn(existing)

        val updatedRecord = BookRecord().apply {
            bookId = 1
            title = "本"
            price = 1000
            publishedDate = LocalDate.now()
            publishingStatus = "PUBLISHED"
        }
        whenever(bookRepository.update(any())).thenReturn(updatedRecord)

        val req = BookUpdateRequest(
            title = null,
            price = null,
            publishingStatus = "PUBLISHED",
            publishedDate = null,
            authorIds = null
        )

        val result = bookService.updateBook(1L, req)

        assertEquals("PUBLISHED", result.publishingStatus)
    }

    @Test
    fun `updateBook - 更新項目が0ならupdateを呼ばない`() {
        val existing = BookRecord().apply {
            bookId = 1
            title = "本"
            price = 1000
            publishingStatus = "UNPUBLISHED"
        }

        whenever(bookRepository.findById(1L)).thenReturn(existing)

        val req = BookUpdateRequest(
            title = null,
            price = null,
            publishingStatus = null,
            publishedDate = null,
            authorIds = null
        )

        val result = bookService.updateBook(1L, req)

        assertEquals("本", result.title)
        verify(bookRepository, never()).update(any())
    }

    @Test
    fun `updateBook - titleだけ更新される`() {
        val existing = BookRecord().apply {
            bookId = 1
            title = "本"
            price = 1000
            publishingStatus = "UNPUBLISHED"
        }

        whenever(bookRepository.findById(1L)).thenReturn(existing)

        val updatedRecord = BookRecord().apply {
            bookId = 1
            title = "新しいタイトル"
            price = 1000
            publishingStatus = "UNPUBLISHED"
        }

        whenever(bookRepository.update(any())).thenReturn(updatedRecord)

        val req = BookUpdateRequest(
            title = "新しいタイトル",
            price = null,
            publishingStatus = null,
            publishedDate = null,
            authorIds = null
        )

        val result = bookService.updateBook(1L, req)

        assertEquals("新しいタイトル", result.title)
        verify(bookRepository, times(1)).update(any())
    }

    @Test
    fun `updateAuthorIds - 著者の再紐付けが成功する`() {
        val existing = BookRecord().apply {
            bookId = 1
            title = "本"
            price = 1000
            publishingStatus = "UNPUBLISHED"
        }

        // 既存書籍の取得
        whenever(bookRepository.findById(1L)).thenReturn(existing)

        // 著者存在チェック
        whenever(authorRepository.findById(1L)).thenReturn(
            AuthorRecord().apply { authorId = 1; name = "A" }
        )
        whenever(authorRepository.findById(2L)).thenReturn(
            AuthorRecord().apply { authorId = 2; name = "B" }
        )

        // 更新後の BookRecord
        val updatedRecord = BookRecord().apply {
            bookId = 1
            title = "本"
            price = 1000
            publishingStatus = "UNPUBLISHED"
        }
        whenever(bookRepository.update(any())).thenReturn(updatedRecord)

        val req = BookUpdateRequest(
            title = null,
            price = null,
            publishingStatus = null,
            publishedDate = null,
            authorIds = listOf(1L, 2L)
        )

        val result = bookService.updateBook(1L, req)

        assertEquals(1, result.bookId)

        // book_author の削除が 1 回
        verify(bookAuthorRepository, times(1)).deleteByBookId(1L)

        // book_author の insert が 2 回
        verify(bookAuthorRepository, times(2)).insert(any())

        // bookRepository.update が 実行されない
        verify(bookRepository, never()).update(any())
    }

    // -----------------------------
    // 異常系
    // -----------------------------

    @Test
    fun `出版済みから未出版へ変更は例外`() {
        val existing = BookRecord().apply {
            bookId = 1
            title = "本"
            price = 1000
            publishingStatus = "PUBLISHED"
        }

        whenever(bookRepository.findById(1L)).thenReturn(existing)

        val req = BookUpdateRequest(
            title = null,
            price = null,
            publishingStatus = "UNPUBLISHED",
            publishedDate = null,
            authorIds = null
        )

        val ex = assertThrows(IllegalArgumentException::class.java) {
            bookService.updateBook(1L, req)
        }

        assertEquals("出版済みの書籍を未出版に戻すことはできません", ex.message)
        verify(bookRepository, never()).update(any())
    }

    @Test
    fun `authorIds に存在しない著者が含まれている場合は例外`() {
        whenever(authorService.exists(1L)).thenReturn(true)
        whenever(authorService.exists(2L)).thenReturn(false)

        val req = BookCreateRequest(
            title = "本",
            price = 1000,
            publishingStatus = "UNPUBLISHED",
            publishedDate = null,
            authorIds = listOf(1L, 2L)
        )

        val ex = assertThrows(NotFoundException::class.java) {
            bookService.createBook(req)
        }

        assertEquals("Author not found: 1", ex.message)
    }

    @Test
    fun `PUBLISHED に更新するのに publishedDate が null なら例外`() {
        val existing = BookRecord().apply {
            bookId = 1
            title = "本"
            price = 1000
            publishingStatus = "UNPUBLISHED"
            publishedDate = null
        }

        whenever(bookRepository.findById(1L)).thenReturn(existing)

        val updatedRecord = BookRecord().apply {
            bookId = 1
            title = "本"
            price = 1000
            publishingStatus = "PUBLISHED"
            publishedDate = null
        }
        whenever(bookRepository.update(any())).thenReturn(updatedRecord)

        val req = BookUpdateRequest(
            publishingStatus = "PUBLISHED",
            publishedDate = null,
            title = null,
            price = null,
            authorIds = null
        )

        val ex = assertThrows(IllegalArgumentException::class.java) {
            bookService.updateBook(1L, req)
        }

        assertEquals("出版済みに更新する場合、publishedDate は必須です", ex.message)
    }

}
