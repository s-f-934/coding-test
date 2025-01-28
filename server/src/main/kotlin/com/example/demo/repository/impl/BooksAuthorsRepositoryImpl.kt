package com.example.demo.repository.impl

import com.example.demo.db.tables.BooksAuthors
import com.example.demo.repository.BooksAuthorsRepository
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.LocalDateTime

/**
 * implementation of BooksAuthorsRepository.
 */
@Repository
class BooksAuthorsRepositoryImpl(
    private val jooq: DSLContext,
    private val clock: Clock = Clock.systemUTC(),
) : BooksAuthorsRepository {

    override fun addAuthors(bookId: Long, authorIds: List<Long>) {
        val now = LocalDateTime.now(clock)
        authorIds.forEach {
            jooq.insertInto(BooksAuthors.BOOKS_AUTHORS,
                BooksAuthors.BOOKS_AUTHORS.BOOK_ID,
                BooksAuthors.BOOKS_AUTHORS.AUTHOR_ID,
                BooksAuthors.BOOKS_AUTHORS.CREATE_AT,
                BooksAuthors.BOOKS_AUTHORS.UPDATE_AT,
                )
                .values(
                    bookId,
                    it,
                    now,
                    now
                )
                .execute()
        }
    }

    override fun deletes(bookId: Long): Int
        = jooq.delete(BooksAuthors.BOOKS_AUTHORS)
            .where(BooksAuthors.BOOKS_AUTHORS.BOOK_ID.eq(bookId))
            .execute()
}