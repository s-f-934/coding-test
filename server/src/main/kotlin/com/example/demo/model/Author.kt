package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Past

/**
 * model of author for get operation api.
 */
data class Author(
    @Schema(example = "1", description = "")
    @get:JsonProperty("id") val id: kotlin.Long? = null,

    @Schema(example = "夏目漱石", description = "")
    @get:JsonProperty("name") val name: kotlin.String? = null,

    @field:Valid
    @field:Past
    @Schema(example = "1916-12-09", description = "")
    @get:JsonProperty("birth") val birth: java.time.LocalDate? = null
    ) {

}

