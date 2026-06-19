package com.sd.laborator.pojo

import java.sql.Time
import java.time.LocalDateTime

data class Person(
    var id: Int = 0,
    var lastName: String = "",
    var firstName: String = "",
    var telephoneNumber: String = "",
    var date_created: LocalDateTime = LocalDateTime.now()
)