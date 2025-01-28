package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.Past

/**
 * model of author for add operation api.
 */
data class AuthorForAdd(

    @Schema(example = "夏目漱石", required = true, description = "")
    @get:JsonProperty("name", required = true) val name: kotlin.String,

    @field:Valid
    @field:Past
    @Schema(example = "1916-12-09", required = true, description = "")
    @get:JsonProperty("birth", required = true) val birth: java.time.LocalDate
    ) {

}

