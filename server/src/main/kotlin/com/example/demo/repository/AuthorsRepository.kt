package com.example.demo.repository

import com.example.demo.entity.Author
import com.example.demo.entity.AuthorForUpdate
import org.springframework.stereotype.Repository

/**
 * repository of authors.
 */
@Repository
interface AuthorsRepository {
    /** get author specify the [id]. */
    fun get(id: Long): Author?

    /** list authors specify the [ids]. */
    fun list(ids: List<Long>): List<Author>

    /** list all authors. */
    fun list(): List<Author>

    /** add author. */
    fun add(author: Author): Author

    /** edit author's information. */
    fun edit(authorForUpdate: AuthorForUpdate): Author?

}