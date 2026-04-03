package dev.tomoki.bookapi.dto.request

import dev.tomoki.bookapi.validator.PublishedDateRequired
import dev.tomoki.bookapi.validator.ValidPublishingStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

@PublishedDateRequired
data class BookCreateRequest(
    @field:NotBlank(message = "タイトルは必須です")
    val title: String?,

    @field:NotNull(message = "価格は必須です")
    @field:Min(value = 0, message = "価格は0以上である必要があります")
    val price: Int?,

    @field:NotBlank(message = "出版ステータスは必須です")
    @field:ValidPublishingStatus
    val publishingStatus: String?,

    val publishedDate: LocalDate?,

    @field:NotNull(message = "著者IDは1件以上必要です")
    @field:Size(min = 1, message = "著者IDは1件以上必要です")
    val authorIds: List<Long>?
)
