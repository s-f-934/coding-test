package com.example.demo.service

import com.example.demo.model.*
import org.springframework.stereotype.Service

/**
 * service of author.
 */
@Service
interface AuthorService {
    /** get author specify the [id]. */
    fun get(id: Long): Author?

    /** list all authors. */
    fun list(): List<Author>

    /** add author. */
    fun add(authorForAdd: AuthorForAdd): Author

    /** edit author. */
    fun edit(id: Long, authorForUpdate: AuthorForUpdate): Author?
}