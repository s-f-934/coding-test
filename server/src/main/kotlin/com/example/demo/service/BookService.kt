package com.example.demo.service

import com.example.demo.model.Book
import com.example.demo.model.BookForAdd
import com.example.demo.model.BookForUpdate
import org.springframework.stereotype.Service

/**
 * service of book.
 */
@Service
interface BookService {
    /** get a book specify the [id]. */
    fun get(id: Long): Book?

    /** list all books. */
    fun list(): List<Book>

    /** add a book. */
    fun add(bookForAdd: BookForAdd): Book

    /** edit a book. */
    fun edit(id: Long, bookForUpdate: BookForUpdate): Book?

    /** publish a book. */
    fun publish(id: Long): Book?

    /** list by author. */
    fun listByAuthor(authorId: Long): List<Book>?

}