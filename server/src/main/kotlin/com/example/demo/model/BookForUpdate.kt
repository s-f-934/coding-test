package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

/**
 * model of book for update operation api.
 * no specify or null value is not change the value.
 */
data class BookForUpdate(

    @Schema(example = "吾輩は猫である", description = "")
    @get:JsonProperty("title") val title: kotlin.String? = null,

    @get:Min(0L)
    @Schema(example = "2000", description = "")
    @get:JsonProperty("price") val price: kotlin.Long? = null,

    @get:Size(min=1)
    @Schema(example = "[1, 2]", description = "")
    @get:JsonProperty("authors") val authors: kotlin.collections.List<kotlin.Long>? = null
    ) {

}

