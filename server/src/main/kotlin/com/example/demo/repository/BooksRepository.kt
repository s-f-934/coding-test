package com.example.demo.repository

import com.example.demo.entity.Book
import com.example.demo.entity.BookForUpdate
import org.springframework.stereotype.Repository

/**
 * repository of books.
 */
@Repository
interface BooksRepository {
    /** get a book specify the [id]. */
    fun get(id: Long): Book?

    /** list all books. */
    fun list(): List<Book>

    /** list by author. */
    fun listByAuthor(authorId: Long): List<Book>

    /** add a book. */
    fun add(book: Book): Book

    /** edit a book. */
    fun edit(bookForUpdate: BookForUpdate): Book?

}