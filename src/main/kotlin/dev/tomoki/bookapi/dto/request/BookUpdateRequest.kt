package dev.tomoki.bookapi.dto.request

import dev.tomoki.bookapi.validator.PublishedDateRequired
import dev.tomoki.bookapi.validator.ValidPublishingStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class BookUpdateRequest(
    @field:Size(min = 1, message = "タイトルは空にできません")
    val title: String?,

    @field:Min(value = 0, message = "価格は0以上である必要があります")
    val price: Int?,

    @field:ValidPublishingStatus
    val publishingStatus: String?,

    val publishedDate: LocalDate?,

    @field:Size(min = 1, message = "著者IDは1件以上必要です")
    val authorIds: List<Long>?
)
