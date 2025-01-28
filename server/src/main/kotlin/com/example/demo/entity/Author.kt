package com.example.demo.entity

import jakarta.validation.constraints.Past
import java.time.LocalDate

/**
 * entity of author for the get and add operation.
 */
data class Author(
    val id: Long,
    val name: String,
    @field:Past
    val birth: LocalDate
) {
    init {
        require(birth.isBefore(LocalDate.now()))
    }
}
