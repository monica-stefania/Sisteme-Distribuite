package com.sd.laborator.expence.pojo

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "expenses")
data class Expense(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var memberId: Long = 0,

    @Column(nullable = false)
    var category: String = "",

    @Column(nullable = false)
    var description: String = "",

    @Column(nullable = false)
    var amount: Double = 0.0,

    @Column(nullable = false)
    var date: String = ""
)
