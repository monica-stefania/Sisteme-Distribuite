package com.sd.laborator.database

import org.springframework.data.jpa.repository.JpaRepository
import java.util.Date
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "stoc_produse")
data class Stoc(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val produs: String = "",
    val stoc_disponibil: Int = 0
)
interface StocRepository: JpaRepository<Stoc, Long>{
    fun findByProdus(produs: String): Stoc?
}
