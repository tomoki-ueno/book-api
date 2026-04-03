package dev.tomoki.bookapi.controller

import dev.tomoki.bookapi.dto.request.BookCreateRequest
import dev.tomoki.bookapi.dto.request.BookUpdateRequest
import dev.tomoki.bookapi.dto.response.BookResponse
import dev.tomoki.bookapi.service.BookService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(
    private val bookService: BookService
) {

    /**
     * 書籍を登録する
     */
    @PostMapping
    fun createBook(@Valid @RequestBody req: BookCreateRequest): BookResponse =
        bookService.createBook(req)

    /**
     * 書籍を更新する
     */
    @PutMapping("/{bookId}")
    fun updateBook(
        @PathVariable bookId: Long,
        @Valid @RequestBody req: BookUpdateRequest
    ): BookResponse =
        bookService.updateBook(bookId, req)

}
