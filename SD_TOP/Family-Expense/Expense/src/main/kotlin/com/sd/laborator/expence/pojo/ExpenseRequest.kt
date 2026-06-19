package com.sd.laborator.expence.pojo

data class ExpenseRequest(
    var memberId: Long = 0,
    var category: String = "",
    var description: String = "",
    var amount: Double = 0.0,
    var date: String = ""
)
