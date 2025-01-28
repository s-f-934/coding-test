package com.example.demo.service.impl

import com.example.demo.entity.BookStatus
import com.example.demo.entity.Book as EBook
import com.example.demo.entity.BookStatus as EBookStatus
import com.example.demo.entity.BookForUpdate as EBookForAdd
import com.example.demo.entity.Author as EAuthor
import com.example.demo.model.Author
import com.example.demo.model.Book.Status
import com.example.demo.model.BookForAdd
import com.example.demo.model.BookForUpdate
import com.example.demo.repository.AuthorsRepository
import com.example.demo.repository.BooksAuthorsRepository
import com.example.demo.repository.BooksRepository
import com.example.demo.service.BookService
import com.example.demo.util.TestUtil
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate
import kotlin.test.*

@SpringBootTest
class BookServiceImplTest {
    @MockitoBean
    private lateinit var booksRepository: BooksRepository

    @MockitoBean
    private lateinit var booksAuthorsRepository: BooksAuthorsRepository

    @MockitoBean
    private lateinit var authorsRepository: AuthorsRepository


    @Autowired
    private lateinit var service: BookService

    @BeforeTest
    fun setUp() {}

    @AfterTest
    fun tearDown() {}

    @Test
    fun get_success() {
        whenever(booksRepository.get(3)).thenReturn(
            EBook(3L, "title3", 2000)
        )
        val book = service.get(3L)

        assertNotNull(book)
        assertEquals(3L, book.id)
        assertEquals("title3", book.title)
        assertEquals(2000, book.price)
        assertEquals(Status.unpublish, book.status)
        assertEquals(0, book.authors!!.size)
    }

    @Test
    fun list_success() {
        whenever(booksRepository.list()).thenReturn(
            listOf(
                EBook(1L, "title1", 1000, BookStatus.unpublish, listOf(
                    EAuthor(1L, "name1", LocalDate.of(2000, 1, 1))
                ))
            )
        )
        val books = service.list()
        assertEquals(1, books.size)
        assertEquals(1L, books[0].id)
        assertEquals("title1", books[0].title)
        assertEquals(1000L, books[0].price)
        assertEquals(Status.unpublish, books[0].status)
        assertNotNull(books[0].authors)
        assertEquals(1, books[0].authors!!.size)
        assertEquals(1L, books[0].authors!![0].id)
        assertEquals("name1", books[0].authors!![0].name)
        assertNotNull(books[0].authors!![0].birth)
        assertEquals(2000, books[0].authors!![0].birth!!.year)
        assertEquals(1, books[0].authors!![0].birth!!.month.value)
        assertEquals(1, books[0].authors!![0].birth!!.dayOfMonth)
    }

    @Test
    fun add_success() {
        val addBookTitle = "added"
        val addBookPrice = 2000L
        whenever(booksRepository.add(TestUtil.any(EBook::class.java))).thenReturn(
            EBook(3L, addBookTitle, addBookPrice)
        )
        Mockito.doNothing().`when`(booksAuthorsRepository).addAuthors(TestUtil.any(Long::class.java), Mockito.anyList())
        whenever(authorsRepository.list(Mockito.anyList())).thenReturn(listOf(
            EAuthor(1L, "name1", LocalDate.of(2000, 1, 1))
        ))
        val authors = listOf(1L)
        val forAdd = BookForAdd("added", 2000L, authors)
        val added = service.add(forAdd)

        assertEquals(3L, added.id)
        assertEquals(addBookTitle, added.title)
        assertEquals(addBookPrice, added.price)
        assertNotNull(added.authors)
        assertEquals(1, added.authors!!.size)
        val author: Author = added.authors!![0]
        assertNotNull(author)
        assertEquals(1L, author.id)
        assertEquals("name1", author.name)
        assertNotNull(author.birth)
        assertEquals(2000, author.birth!!.year)
        assertEquals(1, author.birth!!.month.value)
        assertEquals(1, author.birth!!.dayOfMonth)
    }

    @Test
    fun edit_success() {
        val updateTitle = "updated"
        val updatePrice = 3000L
        val updateAuthors = listOf(2L, 3L)
        whenever(booksRepository.edit(TestUtil.any(EBookForAdd::class.java))).thenReturn(
            EBook(3L, updateTitle, updatePrice, authors = listOf(
                EAuthor(2L, "author1", LocalDate.of(2000, 1, 1)),
                EAuthor(3L, "author2", LocalDate.of(2001, 1, 1)),
            ))
        )
        whenever(authorsRepository.list(Mockito.anyList())).thenReturn(
            listOf(
                EAuthor(2L, "author1", LocalDate.of(2000, 1, 1)),
                EAuthor(3L, "author2", LocalDate.of(2001, 1, 1)),
            )
        )

        val forUpdate = BookForUpdate(updateTitle, updatePrice, updateAuthors)
        val updated = service.edit(3, forUpdate)

        assertNotNull(updated)
        assertEquals(3L, updated.id)
        assertEquals(updateTitle, updated.title)
        assertEquals(updatePrice, updated.price)
        assertEquals(Status.unpublish, updated.status)
        assertNotNull(updated.authors)
        assertEquals(2, updated.authors!!.size)
        val author1: Author = updated.authors!![0]
        val author2: Author = updated.authors!![1]

        assertEquals(2L, author1.id)
        assertEquals("author1", author1.name)
        assertNotNull(author1.birth)
        assertEquals(2000, author1.birth!!.year)
        assertEquals(1, author1.birth!!.month.value)
        assertEquals(1, author1.birth!!.dayOfMonth)

        assertEquals(3L, author2.id)
        assertEquals("author2", author2.name)
        assertNotNull(author2.birth)
        assertEquals(2001, author2.birth!!.year)
        assertEquals(1, author2.birth!!.month.value)
        assertEquals(1, author2.birth!!.dayOfMonth)
    }

    @Test
    fun publish_success() {
        whenever(booksRepository.edit(TestUtil.any(EBookForAdd::class.java))).thenReturn(
            EBook(3L, "title3", 3000L, EBookStatus.published)
        )
        whenever(booksRepository.get(TestUtil.any(Long::class.java))).thenReturn(
            EBook(3L, "title3", 3000L, EBookStatus.published, listOf(
                EAuthor(3L, "name3", LocalDate.of(2000, 1, 1))
            ))
        )

        val published = service.publish(3)

        assertNotNull(published)
        assertEquals(3L, published.id)
        assertEquals("title3", published.title)
        assertEquals(3000L, published.price)
        assertEquals(Status.published, published.status)
    }

    @Test
    fun listByAuthor_success() {
        whenever(booksRepository.listByAuthor(3L)).thenReturn(
            listOf(
                EBook(1L, "title1", 1000),
                EBook(2L, "title2", 2000),
                EBook(2L, "title3", 3000),
            )
        )
        val books = service.listByAuthor(3L)

        assertNotNull(books)
        assertEquals(3, books.size)
    }

}