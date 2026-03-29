package com.sd.laborator.persistence.interfaces

import com.sd.laborator.business.models.Book

interface ILibraryRepository {
    fun createTable()
    fun getAll(): Set<Book>
    fun add(book: Book)
    fun findAllByAuthor(author: String): Set<Book>
    fun findAllByTitle(title: String): Set<Book>
    fun findAllByPublisher(publisher: String): Set<Book>
}