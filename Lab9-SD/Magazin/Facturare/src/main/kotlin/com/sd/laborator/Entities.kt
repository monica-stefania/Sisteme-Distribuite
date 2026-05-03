package com.sd.laborator

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "factura")
data class Factura(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val nr_inregistrare: Long = 0,
    val id_comanda: Long = 0,
    val data: Date = Date()
)

interface FacturaRepository: JpaRepository<Factura, Long>
