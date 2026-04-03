package dev.tomoki.bookapi.request

import dev.tomoki.bookapi.dto.request.BookCreateRequest
import dev.tomoki.bookapi.dto.request.BookUpdateRequest
import dev.tomoki1.bookapi.validator.PublishingStatusConstraintValidator
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BookCreateRequestValidationTest {

    private lateinit var validator: Validator

    @BeforeEach
    fun setup() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    // -----------------------------
    // 正常系
    // -----------------------------

    @Test
    fun `すべて値あり`() {
        val dto = BookCreateRequest(
            title = "タイトル",
            price = 1000,
            publishingStatus = "PUBLISHED",
            publishedDate = LocalDate.now(),
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
    }

    @Test
    fun `出版日は未設定でもOK　（未出版に限る）`() {
        val dto = BookCreateRequest(
            title = "タイトル",
            price = 1000,
            publishingStatus = "UNPUBLISHED",
            publishedDate = null,
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
    }

    @Test
    fun `publishedDateはnullでもOK`() {
        val dto = BookCreateRequest(
            title = "タイトル",
            price = 1000,
            publishingStatus = "INVALID",
            publishedDate = null,
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals(1, violations.size)
    }

    // -----------------------------
    // 異常系
    // -----------------------------

    @Test
    fun `タイトルが空ならエラー`() {
        val dto = BookCreateRequest(
            title = "",
            price = 1000,
            publishingStatus = "PUBLISHED",
            publishedDate = LocalDate.now(),
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals("タイトルは必須です", violations.first().message)
    }

    @Test
    fun `価格がnullならエラー`() {
        val dto = BookCreateRequest(
            title = "タイトル",
            price = null,
            publishingStatus = "PUBLISHED",
            publishedDate = LocalDate.now(),
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals("価格は必須です", violations.first().message)
    }

    @Test
    fun `価格がマイナスならエラー`() {
        val dto = BookCreateRequest(
            title = "タイトル",
            price = -1,
            publishingStatus = "PUBLISHED",
            publishedDate = LocalDate.now(),
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals("価格は0以上である必要があります", violations.first().message)
    }

    @Test
    fun `publishingStatus が不正ならエラー`() {
        val dto = BookCreateRequest(
            title = "タイトル",
            price = 1000,
            publishingStatus = "INVALID",
            publishedDate = LocalDate.now(),
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals("publishingStatus は ${PublishingStatusConstraintValidator.PUBLISHED} または ${PublishingStatusConstraintValidator.UNPUBLISHED} のみ有効です", violations.first().message)
    }

    @Test
    fun `出版ステータスがnullならエラー`() {
        val dto = BookCreateRequest(
            title = "タイトル",
            price = 1000,
            publishingStatus = null,
            publishedDate = LocalDate.now(),
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals("出版ステータスは必須です", violations.first().message)
    }

    @Test
    fun `出版済みなのに publishedDate が null ならエラー`() {
        val dto = BookCreateRequest(
            title = "タイトル",
            price = 1000,
            publishingStatus = "PUBLISHED",
            publishedDate = null,
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals("出版済みの場合、publishedDate は必須です", violations.first().message)
    }

    @Test
    fun `著者が0件ならエラー`() {
        val dto = BookCreateRequest(
            title = "タイトル",
            price = 1000,
            publishingStatus = "PUBLISHED",
            publishedDate = LocalDate.now(),
            authorIds = emptyList()
        )

        val violations = validator.validate(dto)
        assertEquals("著者IDは1件以上必要です", violations.first().message)
    }

    @Test
    fun `著者がNULLならエラー`() {
        val dto = BookCreateRequest(
            title = "タイトル",
            price = 1000,
            publishingStatus = "PUBLISHED",
            publishedDate = LocalDate.now(),
            authorIds = null
        )

        val violations = validator.validate(dto)
        assertEquals("著者IDは1件以上必要です", violations.first().message)
    }

}
