package dev.tomoki.bookapi.controller

import dev.tomoki.bookapi.service.BookService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(
    private val service: BookService
) {
    @GetMapping
    fun getBooks() = service.findAll()
}
