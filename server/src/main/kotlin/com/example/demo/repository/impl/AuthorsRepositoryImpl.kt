package com.example.demo.repository.impl

import com.example.demo.db.tables.Authors
import com.example.demo.db.tables.records.AuthorsRecord
import com.example.demo.entity.Author
import com.example.demo.entity.AuthorForUpdate
import com.example.demo.repository.AuthorsRepository
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.Clock
import java.time.LocalDateTime

/**
 * implementation of AuthorsRepository.
 */
@Repository
class AuthorsRepositoryImpl(
    private val jooq: DSLContext,
    private val clock: Clock = Clock.systemUTC(),
) : AuthorsRepository {

    private fun recordToEntity(it: AuthorsRecord) =
        Author(
            it.id!!,    // not null.
            it.name!!,  // not null.
            it.birth!!) // not null.

    override fun get(id: Long) =
        jooq.selectFrom(Authors.AUTHORS)
            .where(Authors.AUTHORS.ID.eq(id))
            .fetchOne()
            ?.let { recordToEntity(it) }

    override fun list(): List<Author> =
        jooq.selectFrom(Authors.AUTHORS)
            .fetch { recordToEntity(it) }

    override fun list(ids: List<Long>): List<Author> =
        jooq.selectFrom(Authors.AUTHORS)
            .where(Authors.AUTHORS.ID.`in`(ids))
            .fetch { recordToEntity(it) }

    override fun add(author: Author): Author {
        val now = LocalDateTime.now(clock)
        return  jooq.insertInto(
            Authors.AUTHORS,
            Authors.AUTHORS.NAME,
            Authors.AUTHORS.BIRTH,
            Authors.AUTHORS.CREATE_AT,
            Authors.AUTHORS.UPDATE_AT,
        ).values(
                author.name,
                author.birth,
                now,
                now,
            )
            .returning()
            .fetchSingle { recordToEntity(it) }
    }

    override fun edit(authorForUpdate: AuthorForUpdate): Author? {
        val now = LocalDateTime.now(clock)
        return jooq.update(Authors.AUTHORS)
            .set(Authors.AUTHORS.NAME,
                DSL.`when`(DSL.`val`(authorForUpdate.name).isNotNull, authorForUpdate.name)
                .otherwise(Authors.AUTHORS.NAME))
            .set(Authors.AUTHORS.BIRTH,
                DSL.`when`(DSL.`val`(authorForUpdate.birth).isNotNull, authorForUpdate.birth)
                    .otherwise(Authors.AUTHORS.BIRTH))
            .set(Authors.AUTHORS.UPDATE_AT, now)
            .where(Authors.AUTHORS.ID.eq(authorForUpdate.id))
            .returning()
            .fetchOne()
            ?.let { recordToEntity(it) }
    }
}