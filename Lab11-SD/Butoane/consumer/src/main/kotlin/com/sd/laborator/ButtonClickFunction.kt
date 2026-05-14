package com.sd.laborator

import io.micronaut.function.FunctionBean
import jakarta.inject.Inject
import java.lang.Exception
import java.util.function.Consumer

@FunctionBean("consumer")
class ButtonClickFunction: Consumer<QueueButton> {

    @Inject
    lateinit var repository: ButtonClickRepository

    override fun accept(message: QueueButton) {
        println("Button name: ${message.buttonName}")
        try{
            val existingButton = repository.findByButtonName(message.buttonName)
            if(existingButton != null)
            {
                existingButton.clickCount += 1
                repository.update(existingButton)
                println("Updated the button $message to ${existingButton.clickCount} clicks")
            }
            else
            {
                    val newButton = ButtonClick(message.buttonName, 1)
                repository.save(newButton)
                println("Created a new button $message with 1 click")
            }
        }
        catch(e: Exception)
        {
            println("Error: ${e.message}")
        }
    }
}