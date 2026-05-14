package com.sd.laborator

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "clicks")
class ButtonClick(
    @Id
    //var id: Int,
    var buttonName: String = "",
    var clickCount: Int = 1
)