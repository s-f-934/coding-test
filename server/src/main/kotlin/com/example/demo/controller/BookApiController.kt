package com.example.demo.controller

import com.example.demo.model.Book
import com.example.demo.model.BookForAdd
import com.example.demo.model.BookForUpdate
import com.example.demo.service.BookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.jooq.exception.NoDataFoundException
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * controller of book api.
 */
@RestController
@Validated
@RequestMapping("\${api.base-path:/v1}")
class BookApiController(
    private val service: BookService
) {
    /**
     * Add a new book.
     */
    @Operation(
        summary = "Add a new book.",
        operationId = "addBook",
        description = """""",
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation.", content = [Content(schema = Schema(implementation = Book::class))]),
            ApiResponse(responseCode = "400", description = "bad request.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
            ApiResponse(responseCode = "500", description = "internal serer error.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
        ]
    )
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/book"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addBook(
        @Parameter(description = "", required = true)
        @Valid
        @RequestBody
        bookForAdd: BookForAdd
    ): ResponseEntity<Book> =
        ResponseEntity.ok(service.add(bookForAdd))

    /**
     * Finds books by author.
     */
    @Operation(
        summary = "Finds books by a author.",
        operationId = "findBooksByAuthor",
        description = """""",
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation.", content = [Content(array = ArraySchema(schema = Schema(implementation = Book::class)))]),
            ApiResponse(responseCode = "404", description = "not found.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
            ApiResponse(responseCode = "500", description = "internal serer error.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
        ]
    )
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/book/findByAuthor"],
        produces = ["application/json"]
    )
    fun findBooksByAuthor(
        @NotNull
        @Parameter(description = "a Author id to filter by.", required = true)
        @Valid
        @RequestParam(value = "author", required = true)
        author: kotlin.Long
    ): ResponseEntity<List<Book>> =
        ResponseEntity.ok(service.listByAuthor(author))

    /**
     * Finds a book by ID.
     */
    @Operation(
        summary = "Finds a book by ID.",
        operationId = "getBookById",
        description = """Returns a single book.""",
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation.", content = [Content(schema = Schema(implementation = Book::class))]),
            ApiResponse(responseCode = "404", description = "not found.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
            ApiResponse(responseCode = "500", description = "internal serer error.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
        ]
    )
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/book/{Id}"],
        produces = ["application/json"]
    )
    fun getBookById(
        @Parameter(description = "ID of book to return.", required = true)
        @PathVariable("Id")
        id: kotlin.Long
    ): ResponseEntity<Book> {
        val book = service.get(id) ?: throw NoDataFoundException("$id is no exist.")
        return ResponseEntity.ok(book)
    }

    /**
     * Get existing books.
     */
    @Operation(
        summary = "Get existing books.",
        operationId = "getBooks",
        description = """""",
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation.", content = [Content(array = ArraySchema(schema = Schema(implementation = Book::class)))]),
            ApiResponse(responseCode = "500", description = "internal serer error.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
        ]
    )
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/book"],
        produces = ["application/json"]
    )
    fun getBooks(): ResponseEntity<List<Book>> = ResponseEntity.ok(service.list())

    /**
     * Publish a book.
     */
    @Operation(
        summary = "Publish a book.",
        operationId = "publishBook",
        description = """""",
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation", content = [Content(schema = Schema(implementation = Book::class))]),
            ApiResponse(responseCode = "404", description = "not found.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
            ApiResponse(responseCode = "500", description = "internal serer error.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
        ]
    )
    @RequestMapping(
        method = [RequestMethod.PUT],
        value = ["/book/{Id}/publish"],
        produces = ["application/json"]
    )
    fun publishBook(
        @Parameter(description = "Book id to publish.", required = true)
        @PathVariable("Id")
        id: kotlin.Long
    ): ResponseEntity<Book> {
        val book = service.publish(id) ?: throw NoDataFoundException("$id is no exist.")
        return ResponseEntity.ok(book)
    }

    /**
     * Updates a book.
     */
    @Operation(
        summary = "Updates a book.",
        operationId = "updateBook",
        description = """""",
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation.", content = [Content(schema = Schema(implementation = Book::class))]),
            ApiResponse(responseCode = "400", description = "bad request.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
            ApiResponse(responseCode = "404", description = "not found.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
            ApiResponse(responseCode = "500", description = "internal serer error.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
        ]
    )
    @RequestMapping(
        method = [RequestMethod.PUT],
        value = ["/book/{Id}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updateBook(
        @Parameter(description = "ID of book that needs to be updated.", required = true)
        @PathVariable("Id")
        id: kotlin.Long,
        @Parameter(description = "", required = true)
        @Valid
        @RequestBody
        bookForUpdate: BookForUpdate
    ): ResponseEntity<Book> {
        val book = service.edit(id, bookForUpdate) ?: throw NoDataFoundException("$id is no exist.")
        return ResponseEntity.ok(book)
    }
}
