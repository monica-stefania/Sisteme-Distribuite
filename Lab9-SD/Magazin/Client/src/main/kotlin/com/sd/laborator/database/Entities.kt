package com.sd.laborator.database

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "clienti")
data class Client(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val nume: String = "",
    val prenume: String = "",
    @Column(unique = true)
    val email: String = "",
    val parola: String = "",
    val adresaLivrare: String = "",
    val adresaFacturare: String = "",
    val telefon: String = ""
)

@Entity
@Table(name = "catalog_produse")
data class Produs(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val denumire: String = "",
    val pret: Double = 0.0
)

@Entity
@Table(name = "comenzi_neprocesate")
data class ProdusNeprocesat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val denumire: String = ""
)

interface ClientRepository: JpaRepository<Client, Long>
{
    fun findByEmail(email: String): Client?
}
interface CatalogProduseRepository: JpaRepository<Produs, Long>