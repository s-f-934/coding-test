package com.example.demo.service.impl

import com.example.demo.entity.BookStatus
import com.example.demo.model.Author
import com.example.demo.model.Book
import com.example.demo.model.BookForAdd
import com.example.demo.model.BookForUpdate
import com.example.demo.repository.AuthorsRepository
import com.example.demo.repository.BooksAuthorsRepository
import com.example.demo.repository.BooksRepository
import com.example.demo.service.BookService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.demo.entity.BookForUpdate as EBookForUpdate

/**
 * implementation of BookService.
 */
@Service
@Transactional
class BookServiceImpl(
    private val booksRepository: BooksRepository,
    private val authorsRepository: AuthorsRepository,
    private val booksAuthorsRepository: BooksAuthorsRepository
): BookService {

    private fun entityToModel(book: com.example.demo.entity.Book) =
        Book(
            book.id,
            book.title,
            book.price,
            book.authors.map { Author(it.id, it.name, it.birth) },
            Book.Status.forValue(book.status.name)
        )

    override fun get(id: Long): Book? {
        return entityToModel(booksRepository.get(id) ?: return null)
    }

    override fun list(): List<Book> = booksRepository.list().map { entityToModel(it) }

    override fun add(bookForAdd: BookForAdd): Book {
        val book = com.example.demo.entity.Book(
            0,
            bookForAdd.title,
            bookForAdd.price,
        )
        val bookEntity = booksRepository.add(book)

        if (bookForAdd.authors.isNotEmpty()) {
            booksAuthorsRepository.addAuthors(bookEntity.id, bookForAdd.authors)
            bookEntity.authors = authorsRepository.list(bookForAdd.authors)
        }
        return entityToModel(bookEntity)
    }

    override fun edit(id: Long, bookForUpdate: BookForUpdate): Book? {
        val bookForUpdateEntity = com.example.demo.entity.BookForUpdate(
            id = id,
            title = bookForUpdate.title,
            price = bookForUpdate.price,
        )
        val bookEntity = booksRepository.edit(bookForUpdateEntity)

        if (bookEntity?.id != null && bookForUpdate.authors?.isNotEmpty() == true) {
            booksAuthorsRepository.deletes(bookEntity.id)
            booksAuthorsRepository.addAuthors(bookEntity.id, bookForUpdate.authors)
            bookEntity.authors = authorsRepository.list(bookForUpdate.authors)
        }
        return bookEntity?.let { entityToModel(bookEntity) }
    }

    override fun publish(id: Long) =
        booksRepository.edit(
            EBookForUpdate(id, status = BookStatus.published))
            ?.let {
                it.authors = booksRepository.get(id)!!.authors
                entityToModel(it)
            }

    override fun listByAuthor(authorId: Long) =
        booksRepository.listByAuthor(authorId)
            .map { entityToModel(it) }
}