package com.example.demo.repository

import org.springframework.stereotype.Repository

/**
 * repository of books_authors.
 */
@Repository
interface BooksAuthorsRepository {
    /** add relations book and authors. */
    fun addAuthors(bookId: Long, authorIds: List<Long>)

    /** delete relations book and authors. */
    fun deletes(bookId: Long): Int
}