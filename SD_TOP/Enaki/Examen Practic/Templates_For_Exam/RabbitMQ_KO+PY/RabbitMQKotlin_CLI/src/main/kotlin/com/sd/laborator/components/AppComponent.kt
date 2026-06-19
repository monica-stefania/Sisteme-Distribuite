package com.sd.laborator.components

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.system.exitProcess


@Component
class AppComponent {
    // membrii
    // folosind in corutina pentru trimiterea raspunsului
    var awaitingAnswer = AtomicBoolean(false)

    @Autowired
    private lateinit var connectionFactory: RabbitMQConnectionFactoryComponent
    private lateinit var amqpTemplate: AmqpTemplate

    @Autowired
    fun initTemplate() {
        this.amqpTemplate = connectionFactory.rabbitTemplate()
        this.clearQueue()
        //start cli interface
        GlobalScope.launch {
            println("Starting CLI")
            cli()
        }
    }

    @RabbitListener(queues = ["\${sd.rabbitmq.queue}"])
    fun receiveMessage(msg: String) {
        //mesajul este primit ca un sir string -> nu trebuie decodat ca cel de la host
        //Don't uncomment this: val processed_msg = (msg.split(",").map { it.toInt().toChar()}).joinToString(separator="")

        // process message
        print("Mesaj primit: $msg\n")
        awaitingAnswer.set(false)
    }

    fun sendMessage(msg: String) {
        this.amqpTemplate.convertAndSend(connectionFactory.getExchange(), connectionFactory.getRoutingKey(), msg)
        print("[Am trimis un mesaj!]\n")
    }

    fun clearQueue() {
        while(this.amqpTemplate.receive("examen.queue1") != null);
        println("Cleared Queue")
    }

    // Other methods
    private suspend fun cli() {
        //wait 1000 ms for rabbit to initialize
        delay(1000L)
        while (true){
            while (awaitingAnswer.get());
            print("Enter message: ")


            val msg = readLine()
            if (msg == "" || msg == "x" || msg == "stop"){
                break
            }
            //Trimitem mesajul string in formatul ascii numbers separate de virgulitza
            this.sendMessage(msg!!.toCharArray().joinToString(",") { it.toInt().toString() })
            awaitingAnswer.set(true)
        }
        exitProcess(0);
    }
}