package dev.tomoki.bookapi.service

import dev.tomoki.bookapi.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    private val repo: BookRepository
) {
    fun getBooks() = repo.findAll()
}
