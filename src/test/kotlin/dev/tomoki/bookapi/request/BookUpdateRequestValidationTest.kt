package dev.tomoki.bookapi.request

import dev.tomoki.bookapi.validator.ValidPublishingStatus
import dev.tomoki.bookapi.dto.request.BookUpdateRequest
import dev.tomoki1.bookapi.validator.PublishingStatusConstraintValidator
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class BookUpdateRequestValidationTest {

    private lateinit var validator: Validator

    @BeforeEach
    fun setup() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    // -----------------------------
    // 正常系
    // -----------------------------

    @Test
    fun `正常系（全部 null でもOK）`() {
        val dto = BookUpdateRequest(
            title = null,
            price = null,
            publishingStatus = null,
            publishedDate = null,
            authorIds = null
        )

        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
    }

    @Test
    fun `正常系（全部ありでもOK）`() {
        val dto = BookUpdateRequest(
            title = "タイトル",
            price = 9999,
            publishingStatus = "PUBLISHED",
            publishedDate = LocalDate.now(),
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
    }

    // -----------------------------
    // 異常系
    // -----------------------------

    @Test
    fun `title が空文字ならエラー`() {
        val dto = BookUpdateRequest(
            title = "",
            price = null,
            publishingStatus = null,
            publishedDate = null,
            authorIds = null
        )

        val violations = validator.validate(dto)
        assertEquals(1, violations.size)
    }

    @Test
    fun `価格がマイナスならエラー`() {
        val dto = BookUpdateRequest(
            title = null,
            price = -1,
            publishingStatus = null,
            publishedDate = null,
            authorIds = null
        )

        val violations = validator.validate(dto)
        assertEquals(1, violations.size)
    }

    @Test
    fun `publishingStatus が不正ならエラー`() {
        val dto = BookUpdateRequest(
            title = null,
            price = null,
            publishingStatus = "INVALID",
            publishedDate = null,
            authorIds = null
        )

        val violations = validator.validate(dto)
        assertEquals("publishingStatus は ${PublishingStatusConstraintValidator.PUBLISHED} または ${PublishingStatusConstraintValidator.UNPUBLISHED} のみ有効です", violations.first().message)
    }

    @Test
    fun `著者が0件ならエラー`() {
        val dto = BookUpdateRequest(
            title = null,
            price = null,
            publishingStatus = null,
            publishedDate = null,
            authorIds = emptyList()
        )

        val violations = validator.validate(dto)
        assertEquals("著者IDは1件以上必要です", violations.first().message)
    }

}
