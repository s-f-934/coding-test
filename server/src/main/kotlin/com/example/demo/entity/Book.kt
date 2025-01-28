package com.example.demo.entity

import jakarta.validation.constraints.Min

/**
 * entity of book for the get and add operation.
 */
data class Book(
    val id: Long,
    val title: String,
    @get:Min(0L)
    val price: Long,
    val status: BookStatus = BookStatus.unpublish,
    var authors: List<Author> = listOf()
) {
    init {
        require(price > 0)
    }
}