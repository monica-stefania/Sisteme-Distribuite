package com.sd.laborator.pojo

import java.time.LocalDateTime

data class Person(
    var id: Int = 0,
    var lastName: String = "",
    var firstName: String = "",
    var telephoneNumber: String = "",
    var moneyQty: Int = 0,
    var date_created: LocalDateTime = LocalDateTime.now()
)