package com.example.demo.service.impl

import com.example.demo.entity.Author as EAuthor
import com.example.demo.entity.AuthorForUpdate as EAuthorForAdd
import com.example.demo.model.AuthorForAdd
import com.example.demo.model.AuthorForUpdate
import com.example.demo.repository.AuthorsRepository
import com.example.demo.service.AuthorService
import com.example.demo.util.TestUtil
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate
import kotlin.test.*

@SpringBootTest
class AuthorServiceImplTest {

    @MockitoBean
    private lateinit var authorsRepository: AuthorsRepository

    @Autowired
    private lateinit var service: AuthorService

    @BeforeTest
    fun setUp() {}

    @AfterTest
    fun tearDown() {}

    @Test
    fun get_success() {
        whenever(authorsRepository.get(1)).thenReturn(
            EAuthor(1, "author1", LocalDate.of(2000, 3, 31)))
        val author = service.get(1)

        assertNotNull(author)
        assertEquals(1, author.id)
        assertEquals("author1", author.name)
        assertEquals(2000, author.birth!!.year)
        assertEquals(3, author.birth!!.month.value)
        assertEquals(31, author.birth!!.dayOfMonth)
    }

    @Test
    fun list_success() {
        whenever(authorsRepository.list()).thenReturn(listOf(
            EAuthor(1, "author1", LocalDate.of(2000, 1, 5)),
            EAuthor(2, "author2", LocalDate.of(1990, 10, 5))))
        val authors = service.list()

        assertEquals(2, authors.size)
        assertEquals("author1", authors[0].name)
        assertEquals("author2", authors[1].name)
    }

    @Test
    fun add_success() {
        val addAuthorName = "added"
        val addAuthorBirth = LocalDate.of(2000, 1, 1)
        whenever(authorsRepository.add(TestUtil.any(EAuthor::class.java))).thenReturn(
            EAuthor(3, addAuthorName, addAuthorBirth)
        )
        val forAdd = AuthorForAdd(addAuthorName, addAuthorBirth)
        val added = service.add(forAdd)

        assertEquals(3, added.id)
        assertEquals("added", added.name)
        assertEquals(2000, added.birth!!.year)
        assertEquals(1, added.birth!!.month.value)
        assertEquals(1, added.birth!!.dayOfMonth)
    }

    @Test
    fun edit_success() {
        val updateName = "updated"
        val updateBirth = LocalDate.of(1999, 2, 2)
        whenever(authorsRepository.edit(TestUtil.any(EAuthorForAdd::class.java))).thenReturn(
            EAuthor(3, updateName, updateBirth)
        )
        val forUpdate = AuthorForUpdate(updateName, updateBirth)
        val updated = service.edit(3, forUpdate)

        assertNotNull(updated)
        assertEquals(3, updated.id)
        assertEquals("updated", updated.name)
        assertEquals(1999, updated.birth!!.year)
        assertEquals(2, updated.birth!!.month.value)
        assertEquals(2, updated.birth!!.dayOfMonth)
    }
}