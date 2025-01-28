package com.example.demo.controller

import com.example.demo.model.Author
import com.example.demo.model.AuthorForAdd
import com.example.demo.model.AuthorForUpdate
import com.example.demo.service.AuthorService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import org.jooq.exception.NoDataFoundException
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * controller of author api .
 */
@RestController
@Validated
@RequestMapping("\${api.base-path:/v1}")
class AuthorApiController(
    private val service: AuthorService
) {
    /**
     * Add a new author.
     */
    @Operation(
        summary = "Add a new author.",
        operationId = "addAuthor",
        description = """""",
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation.", content = [Content(schema = Schema(implementation = Author::class))]),
            ApiResponse(responseCode = "400", description = "bad request.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
            ApiResponse(responseCode = "500", description = "internal serer error.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
        ]
    )
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/author"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun addAuthor(
        @Parameter(description = "", required = true)
        @Valid
        @RequestBody
        authorForAdd: AuthorForAdd
    ): ResponseEntity<Author> =
        ResponseEntity.ok(service.add(authorForAdd))

    /**
     * Finds a author by ID.
     */
    @Operation(
        summary = "Finds a author by ID.",
        operationId = "getAuthorById",
        description = """Returns a single author.""",
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation.", content = [Content(schema = Schema(implementation = Author::class))]),
            ApiResponse(responseCode = "404", description = "not found.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
            ApiResponse(responseCode = "500", description = "internal serer error.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
        ]
    )
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/author/{Id}"],
        produces = ["application/json"]
    )
    fun getAuthorById(
        @Parameter(description = "ID of author to return.", required = true)
        @PathVariable("Id")
        id: kotlin.Long
    ): ResponseEntity<Author> {
        val author = service.get(id) ?: throw NoDataFoundException("$id is no exist.")
        return ResponseEntity.ok(author)
    }

    /**
     * Get existing authors.
     */
    @Operation(
        summary = "Get existing authors.",
        operationId = "getAuthors",
        description = """""",
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation.", content = [Content(array = ArraySchema(schema = Schema(implementation = Author::class)))]),
            ApiResponse(responseCode = "500", description = "internal serer error.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
        ]
    )
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/author"],
        produces = ["application/json"]
    )
    fun getAuthors(): ResponseEntity<List<Author>> = ResponseEntity.ok(service.list())

    /**
     * Updates a author.
     */
    @Operation(
        summary = "Updates a author.",
        operationId = "updateAuthor",
        description = """""",
        responses = [
            ApiResponse(responseCode = "200", description = "successful operation.", content = [Content(schema = Schema(implementation = Author::class))]),
            ApiResponse(responseCode = "400", description = "bad request.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
            ApiResponse(responseCode = "404", description = "not found.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
            ApiResponse(responseCode = "500", description = "internal serer error.", content = [Content(schema = Schema(implementation = ProblemDetail::class))]),
        ]
    )
    @RequestMapping(
        method = [RequestMethod.PUT],
        value = ["/author/{Id}"],
        produces = ["application/json"],
        consumes = ["application/json"]
    )
    fun updateAuthor(
        @Parameter(description = "ID of author that needs to be updated.", required = true)
        @PathVariable("Id")
        id: kotlin.Long,
        @Parameter(description = "", required = true)
        @Valid
        @RequestBody
        authorForUpdate: AuthorForUpdate
    ): ResponseEntity<Author> {
        val author = service.edit(id, authorForUpdate) ?: throw NoDataFoundException("$id is no exist.")
        return ResponseEntity.ok(author)
    }
}
