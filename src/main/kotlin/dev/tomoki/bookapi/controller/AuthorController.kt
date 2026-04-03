package dev.tomoki.bookapi.controller

import dev.tomoki.bookapi.dto.request.AuthorCreateRequest
import dev.tomoki.bookapi.dto.response.AuthorResponse
import dev.tomoki.bookapi.dto.response.BookResponse
import dev.tomoki.bookapi.dto.request.AuthorUpdateRequest
import dev.tomoki.bookapi.service.AuthorService
import dev.tomoki.bookapi.service.BookService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/authors")
class AuthorController(
    private val authorService: AuthorService,
    private val bookService: BookService
) {

    /**
     * 著者を登録する
     */
    @PostMapping
    fun createAuthor(@Valid @RequestBody req: AuthorCreateRequest): AuthorResponse =
        authorService.createAuthor(req)

    /**
     * 著者を更新する
     */
    @PutMapping("/{authorId}")
    fun updateAuthor(
        @PathVariable authorId: Long,
        @Valid @RequestBody req: AuthorUpdateRequest
    ): AuthorResponse =
        authorService.updateAuthor(authorId, req)

    /**
     * 著者に紐づく書籍一覧を取得する
     */
    @GetMapping("/{authorId}/books")
    fun getBooksByAuthor(@PathVariable authorId: Long): List<BookResponse> =
        bookService.findBooksByAuthor(authorId)

}
