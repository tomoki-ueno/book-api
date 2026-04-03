package dev.tomoki.bookapi.validator

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PublishingStatusValidatorTest {

    private lateinit var validator: Validator

    data class TestDto(
        @field:ValidPublishingStatus
        val publishingStatus: String?
    )

    @BeforeEach
    fun setup() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    // -----------------------------
    // 正常系
    // -----------------------------

    @Test
    fun `PUBLISHED 正常系`() {
        val dto = TestDto("PUBLISHED")
        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
    }

    @Test
    fun `UNPUBLISHED 正常系`() {
        val dto = TestDto("UNPUBLISHED")
        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
    }

    @Test
    fun `null は OK（更新時は許容）は正常系`() {
        val dto = TestDto(null)
        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
    }

    // -----------------------------
    // 異常系
    // -----------------------------

    @Test
    fun `不正な文字列はバリデーションエラー`() {
        val dto = TestDto("INVALID")
        val violations = validator.validate(dto)
        assertEquals("publishingStatus は PUBLISHED または UNPUBLISHED のみ有効です", violations.first().message)
    }

    @Test
    fun `小文字はバリデーションエラー`() {
        val dto = TestDto("published")
        val violations = validator.validate(dto)
        assertEquals("publishingStatus は PUBLISHED または UNPUBLISHED のみ有効です", violations.first().message)
    }

    @Test
    fun `空文字はバリデーションエラー`() {
        val dto = TestDto("")
        val violations = validator.validate(dto)
        assertEquals("publishingStatus は PUBLISHED または UNPUBLISHED のみ有効です", violations.first().message)
    }
}
