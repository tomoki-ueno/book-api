package dev.tomoki.bookapi.validator

import dev.tomoki.bookapi.dto.request.BookCreateRequest
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class PublishedDateRequiredValidatorTest {

    private lateinit var validator: Validator

    @BeforeEach
    fun setup() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    // -----------------------------
    // 正常系
    // -----------------------------

    @Test
    fun `PUBLISHED かつ publishedDate がある場合は成功`() {
        val dto = BookCreateRequest(
            title = "本",
            price = 1000,
            publishingStatus = "PUBLISHED",
            publishedDate = LocalDate.now(),
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
    }

    @Test
    fun `UNPUBLISHED なら publishedDate が null でも成功`() {
        val dto = BookCreateRequest(
            title = "本",
            price = 1000,
            publishingStatus = "UNPUBLISHED",
            publishedDate = null,
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)
        assertEquals(0, violations.size)
    }

    // -----------------------------
    // 異常系
    // -----------------------------

    @Test
    fun `PUBLISHED なのに publishedDate が null ならエラー`() {
        val dto = BookCreateRequest(
            title = "本",
            price = 1000,
            publishingStatus = "PUBLISHED",
            publishedDate = null, // ★ NG
            authorIds = listOf(1L)
        )

        val violations = validator.validate(dto)

        assertEquals(1, violations.size)
        assertEquals(
            "出版済みの場合、publishedDate は必須です",
            violations.first().message
        )
    }
}
