package com.example.demo.repository.impl

import com.example.demo.db.tables.Authors
import com.example.demo.db.tables.Books
import com.example.demo.db.tables.BooksAuthors
import com.example.demo.db.tables.records.BooksRecord
import com.example.demo.entity.Author
import com.example.demo.entity.Book
import com.example.demo.entity.BookForUpdate
import com.example.demo.entity.BookStatus
import com.example.demo.repository.BooksRepository
import org.jooq.*
import org.jooq.impl.DSL
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.select
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * implementation of BooksRepository.
 */
@Repository
class BooksRepositoryImpl(
    private val jooq: DSLContext,
    private val clock: Clock = Clock.systemUTC(),
) : BooksRepository {

    private fun selectBook(): SelectSelectStep<Record5<Long?, String?, Long?, String?, Result<Record3<Long?, String?, LocalDate?>>>>
        = jooq.select(
            Books.BOOKS.ID,
            Books.BOOKS.TITLE,
            Books.BOOKS.PRICE,
            Books.BOOKS.STATUS,
            multiset(
                select(
                    Authors.AUTHORS.ID,
                    Authors.AUTHORS.NAME,
                    Authors.AUTHORS.BIRTH)
                    .from(Authors.AUTHORS)
                    .join(BooksAuthors.BOOKS_AUTHORS)
                    .on(Authors.AUTHORS.ID.eq(BooksAuthors.BOOKS_AUTHORS.AUTHOR_ID))
                    .where(BooksAuthors.BOOKS_AUTHORS.BOOK_ID.eq(Books.BOOKS.ID))
            )
        )

    private fun recordToEntity(it: Record5<Long?, String?, Long?, String?, Result<Record3<Long?, String?, LocalDate?>>>): Book
        = Book(
            id = it.value1()!!,    // not null.
            title = it.value2()!!, // not null.
            price = it.value3()!!, // not null.
            status = BookStatus.valueOf(it.value4()!!), // not null.
            authors = it.value5()!!.map { a -> Author(a.value1()!!, a.value2()!!, a.value3()!!) },
        )

    private fun recordToEntity(it: BooksRecord)
        = Book(
            it.id!!,    // not null.
            it.title!!, // not null.
            it.price!!, // not null.
            BookStatus.valueOf(it.status!!) // not null.
        )

    override fun get(id: Long): Book?
        = selectBook()
            .from(Books.BOOKS)
            .where(Books.BOOKS.ID.eq(id))
            .fetchOne()
            ?.let { recordToEntity(it) }

    override fun list(): List<Book>
        = selectBook()
            .from(Books.BOOKS)
            .fetch { recordToEntity(it) }

    override fun listByAuthor(authorId: Long): List<Book>
        = selectBook()
            .from(Books.BOOKS)
            .join(BooksAuthors.BOOKS_AUTHORS)
            .on(Books.BOOKS.ID.eq(BooksAuthors.BOOKS_AUTHORS.BOOK_ID))
            .where(BooksAuthors.BOOKS_AUTHORS.AUTHOR_ID.eq(authorId))
            .fetch { recordToEntity(it) }

    override fun add(book: Book): Book {
        val now = LocalDateTime.now(clock)
        return jooq.insertInto(
            Books.BOOKS,
            Books.BOOKS.TITLE,
            Books.BOOKS.PRICE,
            Books.BOOKS.STATUS,
            Books.BOOKS.CREATE_AT,
            Books.BOOKS.UPDATE_AT,
        ).values(
            book.title,
            book.price,
            book.status.name,
            now,
            now,
        )
        .returning()
        .fetchSingle { recordToEntity(it) }
    }

    override fun edit(bookForUpdate: BookForUpdate): Book? {
        val now = LocalDateTime.now(clock)
        return jooq.update(Books.BOOKS)
            .set(Books.BOOKS.TITLE,
                DSL.`when`(DSL.`val`(bookForUpdate.title).isNotNull, bookForUpdate.title)
                    .otherwise(Books.BOOKS.TITLE))
            .set(Books.BOOKS.PRICE,
                DSL.`when`(DSL.`val`(bookForUpdate.price).isNotNull, bookForUpdate.price)
                    .otherwise(Books.BOOKS.PRICE))
            .set(Books.BOOKS.STATUS,
                DSL.`when`(DSL.`val`(bookForUpdate.status?.name).isNotNull, bookForUpdate.status?.name)
                    .otherwise(Books.BOOKS.STATUS))
            .set(Books.BOOKS.UPDATE_AT, now)
            .where(Books.BOOKS.ID.eq(bookForUpdate.id))
            .returning()
            .fetchOne()
            ?.let { recordToEntity(it) }
    }
}