package com.sd.laborator

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.Encoders
import java.io.Serializable

// Clasa bean serializabila
class Caracter : Serializable {
    var litera: String? = null
}

fun main(args: Array<String>) {

    val spark = SparkSession.builder()
        .appName("Histograma Caractere SQL")
        .config("spark.master", "local")
        .orCreate

    // Citire fisier ca RDD
    val lines = spark.read()
        .textFile("src/main/resources/ebook.txt")
        .toJavaRDD()

    // Explodam in caractere, filtram litere, lowercase
    val charsRDD = lines.flatMap { line ->
        line.toList()
            .filter { c -> c.isLetter() }
            .map { c ->
                val car = Caracter()
                car.litera = c.lowercaseChar().toString()
                car
            }
            .iterator()
    }

    // Encoder pentru bean-ul nostru
    val encoder = Encoders.bean(Caracter::class.java)

    // Cream Dataset-ul din RDD
    val ds = spark.createDataset(
        charsRDD.rdd(),  // trecem la Scala RDD
        encoder
    )

    // Inregistram ca view SQL temporar
    ds.createOrReplaceTempView("caractere")

    // Interogare SQL
    val histograma = spark.sql("""
        SELECT litera, COUNT(*) as aparitii
        FROM caractere
        GROUP BY litera
        ORDER BY litera ASC
    """.trimIndent())

    println("=== Histograma caracterelor ===")
    histograma.show(26, false)

    spark.stop()
}