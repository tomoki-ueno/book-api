package dev.tomoki.bookapi

import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DatabaseConnectionTest(
        @Autowired private val dsl: DSLContext
) {

    @Test
    fun `postgres is reachable`() {
        val result = dsl.fetchValue("SELECT 1")
        assertEquals(1, result)
    }
}
