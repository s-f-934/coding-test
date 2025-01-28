package com.example.demo.service.impl

import com.example.demo.model.Author
import com.example.demo.model.AuthorForAdd
import com.example.demo.model.AuthorForUpdate
import com.example.demo.repository.AuthorsRepository
import com.example.demo.service.AuthorService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.demo.entity.Author as EAuthor
import com.example.demo.entity.AuthorForUpdate as EAuthorForUpdate

/**
 * implementation of AuthorService.
 */
@Service
@Transactional
class AuthorServiceImpl(
    private val authorsRepository: AuthorsRepository
): AuthorService {

    private fun entityToModel(author: EAuthor) =
        Author(
            author.id,
            author.name,
            author.birth,
        )

    override fun get(id: Long): Author? {
        return entityToModel(authorsRepository.get(id) ?: return null)
    }

    override fun list() = authorsRepository.list().map { entityToModel(it) }

    override fun add(authorForAdd: AuthorForAdd): Author {
        val author = EAuthor(
            0,
            authorForAdd.name,
            authorForAdd.birth,
        )
        return entityToModel(authorsRepository.add(author))
    }

    override fun edit(id: Long, authorForUpdate: AuthorForUpdate): Author? {
        val authorForUpdateEntity = EAuthorForUpdate(
            id = id,
            name = authorForUpdate.name,
            birth = authorForUpdate.birth
        )
        return entityToModel(authorsRepository.edit(authorForUpdateEntity) ?: return null)
    }
}