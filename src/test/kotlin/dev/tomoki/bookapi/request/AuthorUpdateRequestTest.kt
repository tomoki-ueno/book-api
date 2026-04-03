package dev.tomoki.bookapi.request

import dev.tomoki.bookapi.dto.request.AuthorUpdateRequest
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AuthorUpdateRequestTest {

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
        val req = AuthorUpdateRequest(
            name = "芥川龍之介",
            birthDate = LocalDate.of(1892, 3, 1)
        )

        val violations = validator.validate(req)

        assertEquals(0, violations.size)
    }
    @Test
    fun `name が null でもOK（更新なし扱い）`() {
        val req = AuthorUpdateRequest(
            name = null,
            birthDate = null
        )

        val violations = validator.validate(req)

        assertEquals(0, violations.size)
    }

    @Test
    fun `birthDate が null でもOK（更新なし扱い）`() {
        val req = AuthorUpdateRequest(
            name = "太宰治",
            birthDate = null
        )

        val violations = validator.validate(req)

        assertEquals(0, violations.size)
    }

    // -----------------------------
    // 異常系
    // -----------------------------

    @Test
    fun `name が空文字ならエラー`() {
        val req = AuthorUpdateRequest(
            name = "",
            birthDate = null
        )

        val violations = validator.validate(req)

        assertEquals(1, violations.size)
        assertEquals("著者名は空にできません", violations.first().message)
    }

    @Test
    fun `birthDate が未来日ならエラー`() {
        val req = AuthorUpdateRequest(
            name = "夏目漱石",
            birthDate = LocalDate.now().plusDays(1)
        )

        val violations = validator.validate(req)

        assertEquals(1, violations.size)
        assertEquals("生年月日は現在日以前である必要があります", violations.first().message)
    }

}
