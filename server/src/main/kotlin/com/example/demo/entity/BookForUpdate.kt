package com.example.demo.entity

import jakarta.validation.constraints.Min

/**
 * entity of book for the update operation.
 */
data class BookForUpdate(
    val id: Long,
    val title: String? = null,
    @get:Min(0L)
    val price: Long? = null,
    val status: BookStatus? = null,
) {
    init {
        if (price != null) {
            require(price > 0)
        }
    }
}