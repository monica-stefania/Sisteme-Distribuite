package com.sd.laborator.microservices

import com.sd.laborator.interfaces.CachingDAO
import com.sd.laborator.interfaces.LibraryDAO
import com.sd.laborator.interfaces.LibraryPrinter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class LibraryPrinterMicroservice {
    @Autowired
    private lateinit var libraryDAO: LibraryDAO

    @Autowired
    private lateinit var libraryPrinter: LibraryPrinter

    @Autowired
    private lateinit var cachingDAO: CachingDAO


    @RequestMapping("/print", method = [RequestMethod.GET])
    @ResponseBody
    fun customPrint(@RequestParam(required = true, name = "format", defaultValue = "") format: String): String {

        return when(format) {
           "html" -> {
               if(cachingDAO.exists("/print/html") != libraryDAO.getBooks().toString())
                   cachingDAO.addToCache("/print/html", libraryDAO.getBooks().toString())
               return libraryPrinter.printHTML(libraryDAO.getBooks())
           }
           "json" -> {
               if(cachingDAO.exists("/print/json") != libraryDAO.getBooks().toString())
                   cachingDAO.addToCache("/print/json", libraryDAO.getBooks().toString())
               return libraryPrinter.printJSON(libraryDAO.getBooks())
           }
           "raw" -> {
               if(cachingDAO.exists("/print/raw") != libraryDAO.getBooks().toString())
                   cachingDAO.addToCache("/print/raw", libraryDAO.getBooks().toString())
               return libraryPrinter.printRaw(libraryDAO.getBooks())
           }
           else -> {
               if(cachingDAO.exists("/print/*****") != "Not implemented")
                   cachingDAO.addToCache("/print/*****", "Not implemented")
               return "Not implemented"
           }
       }
    }

    @RequestMapping("/find", method = [RequestMethod.GET])
    @ResponseBody
    fun customFind(@RequestParam(required = false, name = "author", defaultValue = "") author: String,
                   @RequestParam(required = false, name = "title", defaultValue = "") title: String,
                   @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String): String {
        if (author != "") {
            if(cachingDAO.exists("/find/$author") != this.libraryDAO.findAllByAuthor(author).toString())
                cachingDAO.addToCache("/find/$author", this.libraryDAO.findAllByAuthor(author).toString())
            return this.libraryPrinter.printJSON(this.libraryDAO.findAllByAuthor(author))
        }
        if (title != "") {
            if(cachingDAO.exists("/find/$title") != this.libraryDAO.findAllByTitle(title).toString())
                cachingDAO.addToCache("/find/$title", this.libraryDAO.findAllByTitle(title).toString())
            return this.libraryPrinter.printJSON(this.libraryDAO.findAllByTitle(title))
        }
        if (publisher != "") {
            if(cachingDAO.exists("/find/$publisher") != this.libraryDAO.findAllByPublisher(publisher).toString())
                cachingDAO.addToCache("/find/$publisher", this.libraryDAO.findAllByPublisher(publisher).toString())
            return this.libraryPrinter.printJSON(this.libraryDAO.findAllByPublisher(publisher))
        }

        if(cachingDAO.exists("/find/*****") != "Not a valid field")
            cachingDAO.addToCache("/find/*****", "Not a valid field")
        return "Not a valid field"
    }
}