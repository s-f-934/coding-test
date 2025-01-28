package com.example.demo.util

import com.example.demo.db.tables.Authors
import com.example.demo.db.tables.Books
import org.jooq.DSLContext
import org.springframework.boot.test.context.TestComponent

@TestComponent
class Deleter(private val jooq: DSLContext) {

    fun deleteAuthor(id: Long) =
        jooq.delete(Authors.AUTHORS)
            .where(Authors.AUTHORS.ID.eq(id))
            .execute()

    fun deleteBook(id: Long) =
        jooq.delete(Books.BOOKS)
        .where(Books.BOOKS.ID.eq(id))
        .execute()
}