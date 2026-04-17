package com.sd.laborator.presentation.controllers

import com.sd.laborator.business.interfaces.ILibraryDAOService
import com.sd.laborator.business.interfaces.ILibraryPrinterService
import com.sd.laborator.business.models.Book
import com.sd.laborator.business.models.Content
import com.sd.laborator.presentation.config.RabbitMqComponent
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.util.concurrent.ArrayBlockingQueue

@Controller
class LibraryPrinterController {
    @Autowired
    private lateinit var _libraryDAOService: ILibraryDAOService

    @Autowired
    private lateinit var _libraryPrinterService: ILibraryPrinterService

    @Autowired
    private lateinit var _rabbitMqComponent: RabbitMqComponent
    private lateinit var _amqpTemplate: AmqpTemplate


    private val _resultQueue = ArrayBlockingQueue<String>(10)
    @Autowired
    fun initTemplate()
    {
        this._amqpTemplate = _rabbitMqComponent.rabbitTemplate()
    }

    @RequestMapping("/print", method = [RequestMethod.GET])
    @ResponseBody
    fun customPrint(@RequestParam(required = true, name = "format", defaultValue = "") format: String): String {
        return when (format) {
            "html" -> _libraryPrinterService.printHTML(_libraryDAOService.getBooks())
            "json" -> _libraryPrinterService.printJSON(_libraryDAOService.getBooks())
            "raw" -> _libraryPrinterService.printRaw(_libraryDAOService.getBooks())
            else -> "Not implemented"
        }
    }

    @RequestMapping("/find", method = [RequestMethod.GET])
    @ResponseBody
    fun customFind(
        @RequestParam(required = false, name = "author", defaultValue = "") author: String,
        @RequestParam(required = false, name = "title", defaultValue = "") title: String,
        @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String
    ): String {
        if (author != "")
            return this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByAuthor(author))
        if (title != "")
            return this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByTitle(title))
        if (publisher != "")
            return this._libraryPrinterService.printJSON(this._libraryDAOService.findAllByPublisher(publisher))
        return "Not a valid field"
    }

    @RequestMapping("/add", method = [RequestMethod.GET])
    @ResponseBody
    fun addBook(
        @RequestParam(required = true, name = "author") author: String,
        @RequestParam(required = true, name = "title") title: String,
        @RequestParam(required = true, name = "publisher") publisher: String,
        @RequestParam(required = true, name = "text") text: String
    ): String
    {
        val book = Book(Content(author, text, title, publisher))
        _libraryDAOService.addBook(book)
        return "Book added with success"
    }

    @RequestMapping("/find-and-print", method = [RequestMethod.GET])
    @ResponseBody
    fun customFindAndPrint(
        @RequestParam(required = false, name = "author", defaultValue = "") author: String,
        @RequestParam(required = false, name = "title", defaultValue = "") title: String,
        @RequestParam(required = false, name = "publisher", defaultValue = "") publisher: String,
        @RequestParam(required = true, name = "format", defaultValue = "") format: String
    ): String {
        val query =  when {
            author.isNotBlank() -> "author=$author&format=$format"
            title.isNotBlank() -> "title=$title&format=$format"
            publisher.isNotBlank() -> "publisher=$publisher&format=$format"
            else -> "format=$format"
        }
        sendQuery(query)
        // asteapta rezulattul maxim 5 secunde
        return _resultQueue.poll(5, java.util.concurrent.TimeUnit.SECONDS)
            ?: "Se incarca rezultatul..."
    }

    @RabbitListener(queues = ["\${libraryapp.rabbitmq.queue.file}"])
    fun fetchFile(message: String)
    {
        //primeste rezultatul
        println("Fisier primit de la Cache: $message")
        _resultQueue.offer(message)
    }

    @RabbitListener(queues = ["\${libraryapp.rabbitmq.queue.command}"])
    fun fetchCommand(message: String)
    {
        print("Command received: $message")
        val parts = message.split("&")
        val typeSearch = parts[0].split("=")
        val format = parts[1].split("=")
        val books: Set<Book> = when(typeSearch[0]) {
            "author" -> _libraryDAOService.findAllByAuthor(typeSearch[1])
            "title" -> _libraryDAOService.findAllByTitle(typeSearch[1])
            "publisher" -> _libraryDAOService.findAllByPublisher(typeSearch[1])
            else -> _libraryDAOService.getBooks()
        }

        if (books.isEmpty()) {
            sendState("$message~Nu au fost gasite rezultate pentru: ${typeSearch[1]}")
            return
        }

        val result = when (format[1]){
            "html" -> _libraryPrinterService.printHTML(books)
            "json" -> _libraryPrinterService.printJSON(books)
            "raw" -> _libraryPrinterService.printRaw(books)
            else -> _libraryPrinterService.printJSON(books)
        }

        sendState("$message~$result")
    }

    fun sendState(message: String)
    {
        println("State to send: $message")
        _amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyState(), message)
    }
    fun sendQuery(message: String)
    {
        println("Query to send: $message")
        this._amqpTemplate.convertAndSend(_rabbitMqComponent.getExchange(), _rabbitMqComponent.getRoutingKeyQuery(), message)
    }

}