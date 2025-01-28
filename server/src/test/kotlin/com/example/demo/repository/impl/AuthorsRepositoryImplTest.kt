package com.example.demo.repository.impl

import com.example.demo.TestcontainersConfiguration
import com.example.demo.entity.Author
import com.example.demo.entity.AuthorForUpdate
import com.example.demo.repository.AuthorsRepository
import com.example.demo.util.Deleter
import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import java.time.Clock
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.test.*


@Import(TestcontainersConfiguration::class)
@JooqTest
class AuthorsRepositoryImplTest {

    @Autowired
    private lateinit var jooq: DSLContext

    private val clockMock: Clock = Clock.fixed(Instant.parse("2000-01-01T00:00:00.00Z"), ZoneId.systemDefault())

    private lateinit var authorsRepository: AuthorsRepository

    private lateinit var deleter: Deleter

    @BeforeTest
    fun setUp() {
        authorsRepository = AuthorsRepositoryImpl(jooq, clockMock)
        deleter = Deleter(jooq)
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun general_success() {
        // before add.
        run {
            val authors = authorsRepository.list()
            assertEquals(1, authors.size) // exist prepare value.
        }

        // do add.
        run {
            // 1
            val author = Author(0, "name1", LocalDate.of(2000, 1, 1))
            val addedAuthor = authorsRepository.add(author)
            assertEquals(2, addedAuthor.id)
            assertEquals("name1", addedAuthor.name)
            assertEquals(2000, addedAuthor.birth.year)
            assertEquals(1, addedAuthor.birth.month.value)
            assertEquals(1, addedAuthor.birth.dayOfMonth)
        }
        run {
            // 2
            val author = Author(0, "name2", LocalDate.of(2001, 1, 1))
            val addedAuthor = authorsRepository.add(author)
            assertEquals(3, addedAuthor.id)
            assertEquals("name2", addedAuthor.name)
            assertEquals(2001, addedAuthor.birth.year)
            assertEquals(1, addedAuthor.birth.month.value)
            assertEquals(1, addedAuthor.birth.dayOfMonth)
        }
        // after add.
        run {
            // list all authors.
            val authors = authorsRepository.list()
            assertEquals(3, authors.size)
        }
        run {
            // list targeted authors.
            val targets = listOf(1L, 2L)
            val authors = authorsRepository.list(targets)
            assertEquals(2, authors.size)
        }

        // delete the added ones.
        run {
            val count = deleter.deleteAuthor(2)
            assertEquals(1, count)
        }
        run {
            val count = deleter.deleteAuthor(3)
            assertEquals(1, count)
        }

        // after delete.
        run {
            val authors = authorsRepository.list()
            assertEquals(1, authors.size)
            assertEquals(1, authors[0].id)
            assertEquals("abcde", authors[0].name) // prepare value.
            assertEquals(2025, authors[0].birth.year) // prepare value.
            assertEquals(1, authors[0].birth.month.value) // prepare value.
            assertEquals(1, authors[0].birth.dayOfMonth) // prepare value.
        }
    }

    @Test
    fun edit_success() {
        // before.
        run {
            val author = authorsRepository.get(1)
            assertNotNull(author)
            assertEquals(1, author.id)
            assertEquals("abcde", author.name) // prepare value.
            assertEquals(2025, author.birth.year) // prepare value.
            assertEquals(1, author.birth.month.value) // prepare value.
            assertEquals(1, author.birth.dayOfMonth) // prepare value.
        }

        // edit.
        run {
            // change the name only.
            val forUpdate = AuthorForUpdate(1, name = "hogehoge")
            val edited = authorsRepository.edit(forUpdate)
            assertNotNull(edited)
            assertEquals(1, edited.id)
            assertEquals("hogehoge", edited.name) // changed the value.
            assertEquals(2025, edited.birth.year) // no change the value.
            assertEquals(1, edited.birth.month.value) // no change the value.
            assertEquals(1, edited.birth.dayOfMonth) // no change the value.
        }
        run {
            // change the birth only.
            val forUpdate = AuthorForUpdate(1, birth = LocalDate.of(2024, 10, 10)) // before now.
            val edited = authorsRepository.edit(forUpdate)
            assertNotNull(edited)
            assertEquals(1, edited.id)
            assertEquals("hogehoge", edited.name) // no change the value.
            assertEquals(2024, edited.birth.year) // changed the value.
            assertEquals(10, edited.birth.month.value) // changed the value.
            assertEquals(10, edited.birth.dayOfMonth) // changed the value.
        }
        run {
            // change the name and birth.
            val forUpdate = AuthorForUpdate(1, name = "fugafuga", birth = LocalDate.of(2023, 9, 9)) // before now.
            val edited = authorsRepository.edit(forUpdate)
            assertNotNull(edited)
            assertEquals(1, edited.id)
            assertEquals("fugafuga", edited.name) // changed the value.
            assertEquals(2023, edited.birth.year) // changed the value.
            assertEquals(9, edited.birth.month.value) // changed the value.
            assertEquals(9, edited.birth.dayOfMonth) // changed the value.
        }
    }
}