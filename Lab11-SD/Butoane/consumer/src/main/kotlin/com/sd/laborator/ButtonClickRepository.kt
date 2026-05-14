package com.sd.laborator

import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository
import java.awt.Button

@Repository
interface ButtonClickRepository: CrudRepository<ButtonClick, String> {
    fun findByButtonName(buttonName: String): ButtonClick?
}