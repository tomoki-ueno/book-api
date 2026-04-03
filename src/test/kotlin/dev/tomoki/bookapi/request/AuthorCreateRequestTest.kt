package dev.tomoki.bookapi.request

import dev.tomoki.bookapi.dto.request.AuthorCreateRequest
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AuthorCreateRequestTest {

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
        val req = AuthorCreateRequest(
            name = "太宰治",
            birthDate = LocalDate.of(1909, 6, 19)
        )

        val violations = validator.validate(req)

        assertEquals(0, violations.size)
    }

    // -----------------------------
    // 異常系
    // -----------------------------

    @Test
    fun `name が空文字ならエラー`() {
        val req = AuthorCreateRequest(
            name = "",
            birthDate = LocalDate.of(1990, 1, 1)
        )

        val violations = validator.validate(req)

        assertEquals(1, violations.size)
        assertEquals("著者名は必須です", violations.first().message)
    }

    @Test
    fun `birthDate がNULLならエラー`() {
        val req = AuthorCreateRequest(
            name = "夏目漱石",
            birthDate = null
        )

        val violations = validator.validate(req)

        assertEquals(1, violations.size)
        assertEquals("生年月日は必須です", violations.first().message)
    }

    @Test
    fun `birthDate が未来日ならエラー`() {
        val req = AuthorCreateRequest(
            name = "夏目漱石",
            birthDate = LocalDate.now().plusDays(1)
        )

        val violations = validator.validate(req)

        assertEquals(1, violations.size)
        assertEquals("生年月日は現在日以前である必要があります", violations.first().message)
    }

}
