package com.example.demo.controller

import com.example.demo.model.Author
import com.example.demo.model.Book
import com.example.demo.model.BookForAdd
import com.example.demo.model.BookForUpdate
import com.example.demo.service.BookService
import com.example.demo.util.TestUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers.equalTo
import org.jooq.exception.DataException
import org.jooq.exception.NoDataFoundException
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.*
import org.springframework.web.servlet.function.RequestPredicates.contentType
import java.time.LocalDate
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@WebMvcTest(BookApiController::class)
class BookApiControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper
) {
    @MockitoBean
    private lateinit var service: BookService

    @BeforeTest
    fun setUp() {
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun list_success() {
        whenever(service.list()).thenReturn(
            listOf(
                Book(1L, "title1", 1000L,
                    listOf(
                        Author(1L, "name1", LocalDate.of(2000, 1, 1)),
                        Author(2L, "name2", LocalDate.of(2001, 1, 1))
                    ),
                    Book.Status.unpublish),
                Book(2L, "title2", 2000L,
                    listOf(
                        Author(3L, "name3", LocalDate.of(2002, 1, 1)),
                        Author(4L, "name4", LocalDate.of(2003, 1, 1))
                    ),
                    Book.Status.published),
            )
        )
        mockMvc.get("/v1/book")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$[0].id", equalTo(1))
                    jsonPath("$[0].title", equalTo("title1"))
                    jsonPath("$[0].price", equalTo(1000))
                    jsonPath("$[0].authors[0].id", equalTo(1))
                    jsonPath("$[0].authors[0].name", equalTo("name1"))
                    jsonPath("$[0].authors[0].birth", equalTo("2000-01-01"))
                    jsonPath("$[0].authors[1].id", equalTo(2))
                    jsonPath("$[0].authors[1].name", equalTo("name2"))
                    jsonPath("$[0].authors[1].birth", equalTo("2001-01-01"))

                    jsonPath("$[1].id", equalTo(2))
                    jsonPath("$[1].title", equalTo("title2"))
                    jsonPath("$[1].price", equalTo(2000))
                    jsonPath("$[1].authors[0].id", equalTo(3))
                    jsonPath("$[1].authors[0].name", equalTo("name3"))
                    jsonPath("$[1].authors[0].birth", equalTo("2002-01-01"))
                    jsonPath("$[1].authors[1].id", equalTo(4))
                    jsonPath("$[1].authors[1].name", equalTo("name4"))
                    jsonPath("$[1].authors[1].birth", equalTo("2003-01-01"))
                }
            }
    }

    @Test
    fun list_success_no_data() {
        whenever(service.list()).thenReturn(
            listOf()
        )
        mockMvc.get("/v1/book")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.length()", equalTo(0))

                }
            }
    }

    @Test
    fun get_success() {
        val getId = 1L
        val getTitle = "title1"
        val getPrice = 1000L
        whenever(service.get(TestUtil.any(Long::class.java))).thenReturn(
            Book(getId, getTitle, getPrice, listOf(
                    Author(1L, "name1", LocalDate.of(2000, 1, 1))
                ),
                Book.Status.unpublish
            )
        )
        mockMvc.get("/v1/book/1")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.id", equalTo(getId.toInt()))
                    jsonPath("$.title", equalTo(getTitle))
                    jsonPath("$.price", equalTo(getPrice.toInt()))
                    jsonPath("$.authors[0].id", equalTo(1))
                    jsonPath("$.authors[0].name", equalTo("name1"))
                    jsonPath("$.authors[0].birth", equalTo("2000-01-01"))
                    jsonPath("$.status", equalTo("unpublish"))
                }
            }

    }

    @Test
    fun get_error_not_found() {
        whenever(service.get(TestUtil.any(Long::class.java))).thenThrow(NoDataFoundException())
        mockMvc.get("/v1/book/1")
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.title", equalTo("Not Found"))
                    jsonPath("$.status", equalTo(404))
                    jsonPath("$.instance", equalTo("/v1/book/1"))
                }
            }
    }

    @Test
    fun add_success() {
        val addId = 2L
        val addTitle = "title2"
        val addPrice = 2000L
        whenever(service.add(TestUtil.any(BookForAdd::class.java))).thenReturn(
            Book(addId, addTitle, addPrice, listOf(
                    Author(1L, "name1", LocalDate.of(2000, 1, 1))
                ),
                Book.Status.unpublish
            )
        )
        val bookForAdd = BookForAdd(addTitle, addPrice, listOf(1L))
        val jsonString = objectMapper.writeValueAsString(bookForAdd)
        mockMvc.post("/v1/book") {
            //accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = jsonString
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.id", equalTo(addId.toInt()))
                    jsonPath("$.title", equalTo(addTitle))
                    jsonPath("$.price", equalTo(addPrice.toInt()))
                    jsonPath("$.authors[0].id", equalTo(1))
                    jsonPath("$.authors[0].name", equalTo("name1"))
                    jsonPath("$.authors[0].birth", equalTo("2000-01-01"))
                    jsonPath("$.status", equalTo("unpublish"))
                }
            }
    }

    @Test
    fun add_error_cannot_insert() {
        val addTitle = "title2"
        val addPrice = 2000L
        whenever(service.add(TestUtil.any(BookForAdd::class.java))).thenThrow(DataException("this is mock."))

        val bookForAdd = BookForAdd(addTitle, addPrice, listOf(1L))
        val jsonString = objectMapper.writeValueAsString(bookForAdd)

        mockMvc.post("/v1/book") {
            //accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = jsonString
        }
            .andDo { print() }
            .andExpect {
                status { isInternalServerError() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.title", equalTo("Internal Server Error"))
                    jsonPath("$.status", equalTo(500))
                    jsonPath("$.instance", equalTo("/v1/book"))
                }
            }
    }

    @Test
    fun edit_success() {
        val updateId = 1L
        val updateTitle = "title2"
        val updatePrice = 2000L
        whenever(service.edit(TestUtil.any(Long::class.java), TestUtil.any(BookForUpdate::class.java))).thenReturn(
            Book(updateId, updateTitle, updatePrice,
                listOf(
                    Author(1L, "name1", LocalDate.of(2000, 1, 1))
                ),
                Book.Status.unpublish
            )
        )
        val bookForUpdate = BookForUpdate(updateTitle, updatePrice)
        val jsonString = objectMapper.writeValueAsString(bookForUpdate)
        mockMvc.put("/v1/book/1") {
            //accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = jsonString
        }
            .andDo { print() }
            .andExpect {
                status { isOk() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.id", equalTo(updateId.toInt()))
                    jsonPath("$.title", equalTo(updateTitle))
                    jsonPath("$.price", equalTo(updatePrice.toInt()))
                    jsonPath("$.authors[0].id", equalTo(1))
                    jsonPath("$.authors[0].name", equalTo("name1"))
                    jsonPath("$.authors[0].birth", equalTo("2000-01-01"))
                    jsonPath("$.status", equalTo("unpublish"))
                }
            }
    }

    @Test
    fun edit_error_not_found() {
        val updateTitle = "title2"
        val updatePrice = 2000L
        whenever(service.edit(TestUtil.any(Long::class.java), TestUtil.any(BookForUpdate::class.java))).thenThrow(NoDataFoundException())
        val bookForUpdate = BookForUpdate(updateTitle, updatePrice)
        val jsonString = objectMapper.writeValueAsString(bookForUpdate)
        mockMvc.put("/v1/book/1") {
            //accept = MediaType.APPLICATION_JSON
            contentType = MediaType.APPLICATION_JSON
            content = jsonString
        }
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.title", equalTo("Not Found"))
                    jsonPath("$.status", equalTo(404))
                    jsonPath("$.instance", equalTo("/v1/book/1"))
                }
            }
    }

    @Test
    fun publish_success() {
        whenever(service.publish(TestUtil.any(Long::class.java))).thenReturn(
            Book(1L, "title1", 2000L,
                listOf(
                    Author(1L, "name1", LocalDate.of(2000, 1, 1))
                ),
                Book.Status.published
            )
        )
        mockMvc.put("/v1/book/1/publish")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.id", equalTo(1))
                    jsonPath("$.title", equalTo("title1"))
                    jsonPath("$.price", equalTo(2000))
                    jsonPath("$.authors[0].id", equalTo(1))
                    jsonPath("$.authors[0].name", equalTo("name1"))
                    jsonPath("$.authors[0].birth", equalTo("2000-01-01"))
                    jsonPath("$.status", equalTo("published"))
                }
            }
    }

    @Test
    fun publish_error_not_found() {
        whenever(service.publish(TestUtil.any(Long::class.java))).thenThrow(NoDataFoundException())
        mockMvc.put("/v1/book/1/publish")
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.title", equalTo("Not Found"))
                    jsonPath("$.status", equalTo(404))
                    jsonPath("$.instance", equalTo("/v1/book/1/publish"))
                }
            }
    }

    @Test
    fun getByAuthor_success() {
        whenever(service.listByAuthor(TestUtil.any(Long::class.java))).thenReturn(
            listOf(
                Book(1L, "title1", 1000L,
                    listOf(
                        Author(1L, "name1", LocalDate.of(2000, 1, 1)),
                        Author(2L, "name2", LocalDate.of(2001, 1, 1))
                    ),
                    Book.Status.unpublish),
                Book(2L, "title2", 2000L,
                    listOf(
                        Author(3L, "name3", LocalDate.of(2002, 1, 1)),
                        Author(4L, "name4", LocalDate.of(2003, 1, 1))
                    ),
                    Book.Status.published),
            )
        )
        mockMvc.get("/v1/book/findByAuthor?author=1")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$[0].id", equalTo(1))
                    jsonPath("$[0].title", equalTo("title1"))
                    jsonPath("$[0].price", equalTo(1000))
                    jsonPath("$[0].authors[0].id", equalTo(1))
                    jsonPath("$[0].authors[0].name", equalTo("name1"))
                    jsonPath("$[0].authors[0].birth", equalTo("2000-01-01"))
                    jsonPath("$[0].authors[1].id", equalTo(2))
                    jsonPath("$[0].authors[1].name", equalTo("name2"))
                    jsonPath("$[0].authors[1].birth", equalTo("2001-01-01"))

                    jsonPath("$[1].id", equalTo(2))
                    jsonPath("$[1].title", equalTo("title2"))
                    jsonPath("$[1].price", equalTo(2000))
                    jsonPath("$[1].authors[0].id", equalTo(3))
                    jsonPath("$[1].authors[0].name", equalTo("name3"))
                    jsonPath("$[1].authors[0].birth", equalTo("2002-01-01"))
                    jsonPath("$[1].authors[1].id", equalTo(4))
                    jsonPath("$[1].authors[1].name", equalTo("name4"))
                    jsonPath("$[1].authors[1].birth", equalTo("2003-01-01"))
                }
            }

    }

    @Test
    fun getByAuthor_error_no_data() {
        whenever(service.listByAuthor(TestUtil.any(Long::class.java))).thenReturn(
            listOf()
        )
        mockMvc.get("/v1/book/findByAuthor?author=1")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.length()", equalTo(0))
                }
            }
    }
}