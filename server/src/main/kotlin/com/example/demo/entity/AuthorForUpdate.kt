package com.example.demo.entity

import jakarta.validation.constraints.Past
import java.time.LocalDate

/**
 * entity of author for the update operation.
 */
data class AuthorForUpdate(
    val id: Long,
    val name: String? = null,
    @field:Past
    val birth: LocalDate? = null
) {
    init {
        if (birth != null) {
            require(birth.isBefore(LocalDate.now()))
        }
    }
}