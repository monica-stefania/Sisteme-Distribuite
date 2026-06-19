package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.ICacheFileService
import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.interfaces.ILibraryPrinterService
import com.sd.laborator.business.models.Cache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class LibraryPrinterController {
    @Autowired
    private lateinit var _libraryDAOService: ILibraryDAOService

    @Autowired
    private lateinit var _libraryPrinterService: ILibraryPrinterService

    @Autowired
    private lateinit var _cacheFileService: ICacheFileService


    @RequestMapping("/print", method = [RequestMethod.GET])
    @ResponseBody
    fun customPrint(@RequestParam(required = true, name = "format", defaultValue = "") format: String): String {
        val query = "/print?format=$format"
        val cached = _cacheFileService.getValidCache(query)
        if (cached != null)
            return "/* HIT PRINT CACHE (FILE) */\n${cached.result}"

        val formattedResult = when (format) {
            "html" -> _libraryPrinterService.printHTML(_libraryDAOService.getBooks())
            "json" -> _libraryPrinterService.printJSON(_libraryDAOService.getBooks())
            "raw" -> _libraryPrinterService.printRaw(_libraryDAOService.getBooks())
            else -> "Not implemented"
        }

        if (formattedResult != "Not implemented") {
            _cacheFileService.addToCache(Cache(query, formattedResult, System.currentTimeMillis()))
        }

        return formattedResult
    }

    @RequestMapping("/find", method = [RequestMethod.GET])
    @ResponseBody
    fun customFind(
        @RequestParam(required = false, name = "author", defaultValue = "") author: String,
        @RequestParam(required = false, name = "title", defaultValue = "") title: String,
        @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String
    ): String {
        val query = when {
            author != "" -> "/find?author=$author"
            title != "" -> "/find?title=$title"
            publisher != "" -> "/find?publisher=$publisher"
            else -> "/find"
        }

        val cached = _cacheFileService.getValidCache(query)

        val formattedResult = when {
            author != "" -> _libraryPrinterService.printJSON(_libraryDAOService.findAllByAuthor(author))
            title != "" -> _libraryPrinterService.printJSON(_libraryDAOService.findAllByTitle(title))
            publisher != "" -> _libraryPrinterService.printJSON(_libraryDAOService.findAllByPublisher(publisher))
            else -> "Not a valid field"
        }
        if (cached != null)
            return "/* HIT FIND CACHE (FILE) */\n${cached.result}"

        if (formattedResult != "Not a valid field") {
            _cacheFileService.addToCache(Cache(query, formattedResult, System.currentTimeMillis()))
        }

        return formattedResult
    }

}