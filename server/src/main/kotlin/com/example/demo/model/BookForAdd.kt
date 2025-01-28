package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

/**
 * model of book for add operation api.
 */
data class BookForAdd(
    @Schema(example = "吾輩は猫である", required = true, description = "")
    @get:JsonProperty("title", required = true) val title: kotlin.String,

    @get:Min(0L)
    @Schema(example = "2000", required = true, description = "")
    @get:JsonProperty("price", required = true) val price: kotlin.Long,

    @get:Size(min=1)
    @Schema(example = "[1, 2]", required = true, description = "")
    @get:JsonProperty("authors", required = true) val authors: kotlin.collections.List<kotlin.Long>
    ) {

}

