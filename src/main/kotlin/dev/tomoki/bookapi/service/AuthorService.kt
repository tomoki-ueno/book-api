package dev.tomoki.bookapi.service

import dev.tomoki.bookapi.dto.request.AuthorCreateRequest
import dev.tomoki.bookapi.dto.response.AuthorResponse
import dev.tomoki.bookapi.dto.request.AuthorUpdateRequest
import dev.tomoki.bookapi.exception.NotFoundException
import dev.tomoki.bookapi.jooq.tables.records.AuthorRecord
import dev.tomoki.bookapi.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorService(
    private val authorRepository: AuthorRepository
) {
    fun exists(authorId: Long): Boolean {
        return authorRepository.findById(authorId) != null
    }

    /**
     * 著者を登録する。
     * returning() により author_id を含むレコードが返る。
     */
    @Transactional
    fun createAuthor(req: AuthorCreateRequest): AuthorResponse {
        val record = AuthorRecord().apply {
            name = req.name
            birthDate = req.birthDate
        }

        val inserted = authorRepository.insert(record)
        return toResponse(inserted)
    }

    /**
     * 著者を更新する。
     * null の項目は更新しない部分更新方式。
     * returning() により author_id を含むレコードが返る。
     */
    @Transactional
    fun updateAuthor(authorId: Long, req: AuthorUpdateRequest): AuthorResponse {
        val existing = authorRepository.findById(authorId)
            ?: throw NotFoundException("Author not found: $authorId")

        // null の項目は更新しない
        val updated = listOfNotNull(
            req.name?.also { existing.name = it },
            req.birthDate?.also { existing.birthDate = it }
        ).let { changes ->
            if (changes.isEmpty()) existing else authorRepository.update(existing)
        }
        return toResponse(updated)
    }

    private fun toResponse(record: AuthorRecord) = AuthorResponse(
        authorId = record.authorId!!,
        name = record.name!!,
        birthDate = record.birthDate!!
    )
}
