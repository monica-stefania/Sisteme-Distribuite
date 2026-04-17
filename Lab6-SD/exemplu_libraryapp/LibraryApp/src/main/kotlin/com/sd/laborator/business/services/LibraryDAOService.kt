package com.sd.laborator.business.services

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.models.Book
import com.sd.laborator.business.models.Content
import com.sd.laborator.persistence.interfaces.ILibraryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class LibraryDAOService : ILibraryDAOService {
    @Autowired
    private lateinit var _libraryRepository: ILibraryRepository

    @PostConstruct
    fun init()
    {
        _libraryRepository.createTable()
        if(_libraryRepository.getAll().isEmpty())
        {
            _libraryRepository.add(Book(
                Content(
                    "Jules Verne",
                    "Steaua Sudului",
                    "Corint",
                    "Nemaipomeniti sunt francezii astia! - Vorbiti, domnule, va ascult! ...."
                    )
            ))
            _libraryRepository.add(Book(
                Content(
                    "Jules Verne",
                    "O calatorie spre centrul pamantului",
                    "Polirom",
                    "Cuvant Inainte. Imaginatia copiilor - zicea un mare poet romantic spaniol - este asemenea unui cal nazdravan, iar curiozitatea lor e pintenul ce-l fugareste prin lumea celor mai indraznete proiecte.")
            ))
            _libraryRepository.add(Book(
                Content(
                    "Jules Verne",
                    "Insula Misterioasa",
                    "Teora",
                    "Partea intai. Naufragiatii vazduhului. Capitolul 1. Uraganul din 1865. ...")
            ))

            _libraryRepository.add(Book(
                Content("Jules Verne",
                "Casa cu aburi",
                "Albatros",
                "Capitolul I. S-a pus un premiu pe capul unui om. Se ofera premiu de 2000 de lire ...")
            ))

            _libraryRepository.add(Book(
                Content(
                    "Roberto Ierusalimschy",
                    "Programming in LUA",
                    "Teora",
                    "Preface. When Waldemar, Luiz, and I started the development of Lua, back in 1993, we could hardly imagine that it would spread as it did. ...")
            ))
        }
    }
    /*private var _books: MutableSet<Book> = mutableSetOf(
        Book(
            Content(
                "Roberto Ierusalimschy",
                "Preface. When Waldemar, Luiz, and I started the development of Lua, back in 1993, we could hardly imagine that it would spread as it did. ...",
                "Programming in LUA",
                "Teora"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Nemaipomeniti sunt francezii astia! - Vorbiti, domnule, va ascult! ....",
                "Steaua Sudului",
                "Corint"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Cuvant Inainte. Imaginatia copiilor - zicea un mare poet romantic spaniol - este asemenea unui cal nazdravan, iar curiozitatea lor e pintenul ce-l fugareste prin lumea celor mai indraznete proiecte.",
                "O calatorie spre centrul pamantului",
                "Polirom"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Partea intai. Naufragiatii vazduhului. Capitolul 1. Uraganul din 1865. ...",
                "Insula Misterioasa",
                "Teora"
            )
        ),
        Book(
            Content(
                "Jules Verne",
                "Capitolul I. S-a pus un premiu pe capul unui om. Se ofera premiu de 2000 de lire ...",
                "Casa cu aburi",
                "Albatros"
            )
        )
    )*/

    override fun createTable() {
        _libraryRepository.createTable()
    }

    override fun getBooks(): Set<Book> {
        return _libraryRepository.getAll()
    }

    override fun addBook(book: Book) {
        _libraryRepository.add(book)
    }

    override fun findAllByAuthor(author: String): Set<Book> {
        return _libraryRepository.findAllByAuthor(author)
    }

    override fun findAllByTitle(title: String): Set<Book> {
        return _libraryRepository.findAllByTitle(title)
    }

    override fun findAllByPublisher(publisher: String): Set<Book> {
        return _libraryRepository.findAllByPublisher(publisher)
    }
}