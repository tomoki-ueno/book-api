package dev.tomoki.bookapi.service

import dev.tomoki.bookapi.dto.request.AuthorCreateRequest
import dev.tomoki.bookapi.dto.request.AuthorUpdateRequest
import dev.tomoki.bookapi.exception.NotFoundException
import dev.tomoki.bookapi.jooq.tables.records.AuthorRecord
import dev.tomoki.bookapi.repository.AuthorRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.LocalDate

class AuthorServiceTest {

    private val authorRepository = mock<AuthorRepository>()
    private val authorService = AuthorService(authorRepository)

    // -----------------------------
    // 正常系
    // -----------------------------

    @Test
    fun `exists - 著者が存在する場合 true`() {
        whenever(authorRepository.findById(1L))
            .thenReturn(AuthorRecord().apply { authorId = 1; name = "著者" })

        val result = authorService.exists(1L)

        assertTrue(result)
    }

    @Test
    fun `exists - 著者が存在しない場合 false`() {
        whenever(authorRepository.findById(1L)).thenReturn(null)

        val result = authorService.exists(1L)

        assertFalse(result)
    }

    @Test
    fun `createAuthor - 正常に作成できる`() {
        val req = AuthorCreateRequest(
            name = "夏目漱石",
            birthDate = LocalDate.of(1867, 2, 9)
        )

        val savedRecord = AuthorRecord().apply {
            authorId = 1
            name = req.name
            birthDate = req.birthDate
        }

        whenever(authorRepository.insert(any())).thenReturn(savedRecord)

        val result = authorService.createAuthor(req)

        assertEquals(1, result.authorId)
        assertEquals("夏目漱石", result.name)
        verify(authorRepository, times(1)).insert(any())
    }

    @Test
    fun `updateAuthor - nameだけ更新される`() {
        val existing = AuthorRecord().apply {
            authorId = 1
            name = "元の名前"
            birthDate = LocalDate.of(1900, 1, 1)
        }

        whenever(authorRepository.findById(1L)).thenReturn(existing)

        // 新しいレコードを作る
        val updatedRecord = AuthorRecord().apply {
            authorId = 1
            name = "新しい名前"
            birthDate = existing.birthDate
        }
        whenever(authorRepository.update(any())).thenReturn(updatedRecord)

        val req = AuthorUpdateRequest(name = "新しい名前", birthDate = null)

        val result = authorService.updateAuthor(1L, req)

        assertEquals("新しい名前", result.name)
        verify(authorRepository, times(1)).update(any())
    }

    @Test
    fun `updateAuthor - birthDateだけ更新される`() {
        val existing = AuthorRecord().apply {
            authorId = 1
            name = "元の名前"
            birthDate = LocalDate.of(1900, 1, 1)
        }

        whenever(authorRepository.findById(1L)).thenReturn(existing)

        val newDate = LocalDate.of(2000, 1, 1)

        // 新しいレコードを作る
        val updatedRecord = AuthorRecord().apply {
            authorId = 1
            name = existing.name
            birthDate = newDate
        }
        whenever(authorRepository.update(any())).thenReturn(updatedRecord)

        val req = AuthorUpdateRequest(name = null, birthDate = newDate)

        val result = authorService.updateAuthor(1L, req)

        assertEquals(newDate, result.birthDate)
        verify(authorRepository, times(1)).update(any())
    }

    @Test
    fun `updateAuthor - nameとbirthDate両方更新される`() {
        val existing = AuthorRecord().apply {
            authorId = 1
            name = "元の名前"
            birthDate = LocalDate.of(1900, 1, 1)
        }

        whenever(authorRepository.findById(1L)).thenReturn(existing)

        val newDate = LocalDate.of(2000, 1, 1)

        // ★ copy() は使えないので、新しいレコードを作る
        val updatedRecord = AuthorRecord().apply {
            authorId = 1
            name = "新しい名前"
            birthDate = newDate
        }
        whenever(authorRepository.update(any())).thenReturn(updatedRecord)

        val req = AuthorUpdateRequest(name = "新しい名前", birthDate = newDate)

        val result = authorService.updateAuthor(1L, req)

        assertEquals("新しい名前", result.name)
        assertEquals(newDate, result.birthDate)
        verify(authorRepository, times(1)).update(any())
    }

    @Test
    fun `updateAuthor - nameとbirthDateがnullなら更新しない`() {
        val existing = AuthorRecord().apply {
            authorId = 1
            name = "元の名前"
            birthDate = LocalDate.of(1900, 1, 1)
        }

        whenever(authorRepository.findById(1L)).thenReturn(existing)

        val req = AuthorUpdateRequest(name = null, birthDate = null)

        val result = authorService.updateAuthor(1L, req)

        // 更新なし → existing のまま返す
        assertEquals("元の名前", result.name)
        verify(authorRepository, never()).update(any())
    }

    // -----------------------------
    // 異常系
    // -----------------------------

    @Test
    fun `updateAuthor - 存在しないIDなら例外`() {
        whenever(authorRepository.findById(1L)).thenReturn(null)

        val req = AuthorUpdateRequest(name = "新しい名前", birthDate = null)

        val ex = assertThrows(NotFoundException::class.java) {
            authorService.updateAuthor(1L, req)
        }

        assertEquals("Author not found: 1", ex.message)
        verify(authorRepository, never()).update(any())
    }

}
