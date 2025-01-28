package com.example.demo.controller

import com.example.demo.model.Author
import com.example.demo.model.AuthorForAdd
import com.example.demo.model.AuthorForUpdate
import com.example.demo.service.AuthorService
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


@WebMvcTest(AuthorApiController::class)
class AuthorApiControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper
) {

    @MockitoBean
    private lateinit var service: AuthorService

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
                Author(1L,"aaa", LocalDate.of(2000, 1, 1)),
                Author(2L,"bbb", LocalDate.of(2001, 2, 2)),
            )
        )

        mockMvc.get("/v1/author")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$[0].id", equalTo(1))
                    jsonPath("$[0].name", equalTo("aaa"))
                    jsonPath("$[0].birth", equalTo("2000-01-01"))
                    jsonPath("$[1].id", equalTo(2))
                    jsonPath("$[1].name", equalTo("bbb"))
                    jsonPath("$[1].birth", equalTo("2001-02-02"))
                }
            }
    }

    @Test
    fun list_success_no_data() {
        whenever(service.list()).thenReturn(
            listOf()
        )
        mockMvc.get("/v1/author")
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
        val getName = "getname"
        val getBirth = LocalDate.of(2002, 1, 1)
        whenever(service.get(getId)).thenReturn(
            Author(getId, getName, getBirth)
        )
        mockMvc.get("/v1/author/1")
            .andDo { print() }
            .andExpect {
                status { isOk() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.id", equalTo(getId.toInt()))
                    jsonPath("$.name", equalTo(getName))
                    jsonPath("$.birth", equalTo(getBirth.toString()))
                }
            }
    }

    @Test
    fun get_error_not_found() {
        whenever(service.get(TestUtil.any(Long::class.java))).thenThrow(NoDataFoundException())
        mockMvc.get("/v1/author/1")
            .andDo { print() }
            .andExpect {
                status { isNotFound() }
                contentType(MediaType.APPLICATION_JSON)
                content {
                    jsonPath("$.title", equalTo("Not Found"))
                    jsonPath("$.status", equalTo(404))
                    jsonPath("$.instance", equalTo("/v1/author/1"))
                }
            }
    }

    @Test
    fun add_success() {
        val addId = 1L
        val addName = "name1"
        val addBirth = LocalDate.of(2000, 1, 1)
        whenever(service.add(TestUtil.any(AuthorForAdd::class.java))).thenReturn(
            Author(addId, addName, addBirth)
        )
        val authorForAdd = AuthorForAdd(addName, addBirth)
        val jsonString = objectMapper.writeValueAsString(authorForAdd)
        mockMvc.post("/v1/author") {
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
                    jsonPath("$.name", equalTo(addName))
                    jsonPath("$.birth", equalTo(addBirth.toString()))
                }
            }
    }

    @Test
    fun add_error_cannot_insert() {
        val addName = "name1"
        val addBirth = LocalDate.of(2000, 1, 1)
        whenever(service.add(TestUtil.any(AuthorForAdd::class.java))).thenThrow(DataException("this is mock."))
        val authorForAdd = AuthorForAdd(addName, addBirth)
        val jsonString = objectMapper.writeValueAsString(authorForAdd)
        mockMvc.post("/v1/author") {
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
                    jsonPath("$.detail", equalTo("this is mock."))
                    jsonPath("$.instance", equalTo("/v1/author"))
                }
            }
    }

    @Test
    fun edit_success() {
        val updateId = 1L
        val updateName = "name2"
        val updateBirth = LocalDate.of(2001, 1, 1)
        whenever(service.edit(TestUtil.any(Long::class.java), TestUtil.any(AuthorForUpdate::class.java))).thenReturn(
            Author(updateId, updateName, updateBirth)
        )
        val authorForUpdate = AuthorForUpdate(updateName, updateBirth)
        val jsonString = objectMapper.writeValueAsString(authorForUpdate)
        mockMvc.put("/v1/author/1") {
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
                    jsonPath("$.name", equalTo(updateName))
                    jsonPath("$.birth", equalTo(updateBirth.toString()))
                }
            }
    }

    @Test
    fun edit_error_not_found() {
        val updateName = "name2"
        val updateBirth = LocalDate.of(2001, 1, 1)
        whenever(service.edit(TestUtil.any(Long::class.java), TestUtil.any(AuthorForUpdate::class.java))).thenThrow(NoDataFoundException())
        val authorForUpdate = AuthorForUpdate(updateName, updateBirth)
        val jsonString = objectMapper.writeValueAsString(authorForUpdate)
        mockMvc.put("/v1/author/1") {
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
                    jsonPath("$.instance", equalTo("/v1/author/1"))
                }
            }
    }
}
