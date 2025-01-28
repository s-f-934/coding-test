package com.example.demo.repository.impl

import com.example.demo.FlywayMigrationConfiguration
import com.example.demo.TestcontainersConfiguration
import com.example.demo.entity.Author
import com.example.demo.entity.Book
import com.example.demo.entity.BookForUpdate
import com.example.demo.entity.BookStatus
import com.example.demo.repository.AuthorsRepository
import com.example.demo.repository.BooksAuthorsRepository
import com.example.demo.repository.BooksRepository
import com.example.demo.util.Deleter
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.*

@Import(TestcontainersConfiguration::class,
    FlywayMigrationConfiguration::class)
@JooqTest
class BooksRepositoryImplTest {

    @Autowired
    private lateinit var jooq: DSLContext

    private val clockMock: Clock = Clock.fixed(Instant.parse("2000-01-01T00:00:00.00Z"), ZoneId.systemDefault())

    private lateinit var booksRepository: BooksRepository

    private lateinit var authorsRepository: AuthorsRepository

    private lateinit var bookAuthorsRepository: BooksAuthorsRepository

    private lateinit var deleter: Deleter

    @BeforeTest
    fun setUp() {
        booksRepository = BooksRepositoryImpl(jooq, clockMock)
        authorsRepository = AuthorsRepositoryImpl(jooq, clockMock)
        bookAuthorsRepository = BooksAuthorsRepositoryImpl(jooq, clockMock)
        deleter = Deleter(jooq)
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun general_success() {
        // before.
        run {
            val books = booksRepository.list()
            assertEquals(1, books.size) // exist prepare value.
        }

        // do add.
        val addedBookId1 = run {
            // 1
            val book = Book(0, "title1", 1000L)
            val addedBook = booksRepository.add(book)
            //assertEquals(2, addedBook.id)
            assertEquals("title1", addedBook.title)
            assertEquals(1000L, addedBook.price)
            assertEquals("unpublish", addedBook.status.name)
            assertEquals(0, addedBook.authors.size)
            addedBook.id
        }
        val addedBookId2 = run {
            // 2
            val book = Book(0, "title2", 2000L)
            val addedBook = booksRepository.add(book)
            //assertEquals(3, addedBook.id)
            assertEquals("title2", addedBook.title)
            assertEquals(2000L, addedBook.price)
            assertEquals("unpublish", addedBook.status.name)
            assertEquals(0, addedBook.authors.size)
            addedBook.id
        }
        // after add.
        run {
            // list all authors.
            val books = booksRepository.list()
            assertEquals(3, books.size)
        }

        // delete the added ones.
        run {
            val count = deleter.deleteBook(addedBookId1)
            assertEquals(1, count)
        }
        run {
            val count = deleter.deleteBook(addedBookId2)
            assertEquals(1, count)
        }

        // after delete.
        run {
            // list all authors.
            val books = booksRepository.list()
            assertEquals(1, books.size)
        }
    }

    @Test
    fun edit_success() {
        // before.
        run {
            val book = booksRepository.get(1)
            assertNotNull(book)
            assertEquals(1, book.id)
            assertEquals("abcde", book.title)
            assertEquals(1000L, book.price)
            assertEquals("unpublish", book.status.name)
            assertEquals(1, book.authors.size)
        }

        // edit.
        run {
            // change the title only.
            val forUpdate = BookForUpdate(1, title = "hogehoge")
            val edited = booksRepository.edit(forUpdate)
            assertNotNull(edited)
            assertEquals(1, edited.id)
            assertEquals("hogehoge", edited.title) // changed the value.
            assertEquals(1000L, edited.price) // no change the value.
            assertEquals("unpublish", edited.status.name) // no change the value.
            assertEquals(0, edited.authors.size) // no change the value.
        }
        run {
            // change the price only.
            val forUpdate = BookForUpdate(1, price = 2000L)
            val edited = booksRepository.edit(forUpdate)
            assertNotNull(edited)
            assertEquals(1, edited.id)
            assertEquals("hogehoge", edited.title) // no change the value.
            assertEquals(2000L, edited.price) // changed the value.
            assertEquals("unpublish", edited.status.name) // no change the value.
            assertEquals(0, edited.authors.size) // no change the value.
        }
        run {
            // change the status only.
            val forUpdate = BookForUpdate(1, status = BookStatus.published)
            val edited = booksRepository.edit(forUpdate)
            assertNotNull(edited)
            assertEquals(1, edited.id)
            assertEquals("hogehoge", edited.title) // no change the value.
            assertEquals(2000L, edited.price) // no change the value.
            assertEquals("published", edited.status.name) // changed the value.
            assertEquals(0, edited.authors.size) // no change the value.
        }
    }

    @Test
    fun listByAuthor_success() {
        // before.
        run {
            val books = booksRepository.list()
            assertEquals(1, books.size)
            val list = booksRepository.listByAuthor(1)
            assertEquals(1, list.size)
        }

        // prepare.
        val (newBookId, newAuthorId) = run {
            // add a new book.
            val newBook = Book(0, "title1", 3000L)
            val addedBook = booksRepository.add(newBook)
            // add a new author.
            val newAuthor = Author(0, "name1", LocalDate.of(2000, 2, 3))
            val addedAuthor = authorsRepository.add(newAuthor)

            // set relation.
            bookAuthorsRepository.addAuthors(1, listOf(1, addedAuthor.id))
            bookAuthorsRepository.addAuthors(addedBook.id, listOf(addedAuthor.id))

            addedBook.id to addedAuthor.id
        }

        // test.
        run {
            val list = booksRepository.listByAuthor(newAuthorId)
            assertEquals(2, list.size)
            assertEquals(1, list[0].id)
            assertEquals(newBookId, list[1].id)
        }

        // cleanup.
        run {
            deleter.deleteBook(newBookId)
            deleter.deleteAuthor(newAuthorId)
        }
    }
}