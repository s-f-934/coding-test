package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.Valid

/**
 * model of book for get operation api.
 */
data class Book(

    @Schema(example = "1", description = "")
    @get:JsonProperty("id") val id: kotlin.Long? = null,

    @Schema(example = "吾輩は猫である", description = "")
    @get:JsonProperty("title") val title: kotlin.String? = null,

    @get:Min(0L)
    @Schema(example = "2000", description = "")
    @get:JsonProperty("price") val price: kotlin.Long? = null,

    @field:Valid
    @Schema(/*example = "[1, 2]",*/ description = "")
    @get:JsonProperty("authors") val authors: kotlin.collections.List<Author>? = null,

    @Schema(example = "unpublish", description = "book status")
    @get:JsonProperty("status") val status: Status? = Status.unpublish
    ) {

    /**
    * book status
    * Values: unpublish,published
    */
    enum class Status(@get:JsonValue val value: kotlin.String) {

        unpublish("unpublish"),
        published("published");

        companion object {
            @JvmStatic
            @JsonCreator
            fun forValue(value: kotlin.String): Status {
                return values().first{it -> it.value == value}
            }
        }
    }

}

