
package com.sd.laborator.microservices

import com.sd.laborator.components.RabbitMQConnectionFactoryComponent
import com.sd.laborator.interfaces.DatabaseInterface
import com.sd.laborator.model.Person
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.lang.Exception

@Controller
class AppService {
    // membrii

    // @Autowired servicii
    @Autowired
    private lateinit var databaseDAO: DatabaseInterface

    @Autowired
    private lateinit var connectionFactory: RabbitMQConnectionFactoryComponent
    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
        this.clearQueue()
        this.databaseDAO.createQueryTable()
    }

    @RequestMapping("/find", method = [RequestMethod.GET])
    @ResponseBody
    fun customFind(@RequestParam(required = false, name = "firstName", defaultValue = "") firstName: String) : String{
        var result: String
        if (firstName != ""){
            val listPers = this.databaseDAO.findAllByFirstName(firstName)
           return listPers.joinToString(prefix = "{ ", postfix = " }") { it.toString() }
        }
        return "Wrong Request"
    }

    @RabbitListener(queues = ["\${sd.rabbitmq.queue}"])
    fun receiveMessage(msg: String) {
        // mesajul este primit ca un sir de coduri ASCII -> necesita decodare
        val processed_msg = (msg.split(",").map { it.toInt().toChar()}).joinToString(separator="").split(" ")

        // process message
        print("Mesaj primit: $processed_msg\n")
        try{
            val age = processed_msg[2].toInt()
            this.databaseDAO.insertPerson(Person(-1, processed_msg[0], processed_msg[1], age, "whatever"))
            sendMessage("Insertion Succesfull")

        } catch(e: Exception){
            sendMessage("Invalid Data")
            throw e
        }

    }


    fun sendMessage(msg: String) {
        this.amqpTemplate.convertAndSend(connectionFactory.getExchange(), connectionFactory.getRoutingKey(), msg)
        print("[Am trimis un mesaj!]\n")
    }


    fun clearQueue() {
        while(this.amqpTemplate.receive("examen.queue") != null);
        println("Cleared Queue")
    }
    // Other methods
}