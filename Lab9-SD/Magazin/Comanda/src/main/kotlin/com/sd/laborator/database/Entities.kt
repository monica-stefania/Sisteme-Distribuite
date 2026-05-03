package com.sd.laborator.database

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "comanda")
data class Comanda(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val id_client: Long = 0,
    val produs: String = "",
    val cantitate: Long = 0
)

interface ComandaRepository: JpaRepository<Comanda, Long>
