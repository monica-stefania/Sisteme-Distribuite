package com.sd.laborator

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import scala.Tuple2

fun main(args: Array<String>) {
    val sparkConf = SparkConf().setMaster("local[*, 6]").setAppName("Spark RDD")
    val sparkContext = JavaSparkContext(sparkConf)

    val lines = sparkContext.textFile("src/main/resources/ebook.txt")

    val characters = lines
        .flatMap { line -> line.toList().iterator() }
        .filter { char -> char.isLetter() }
        .map { char -> char.lowercase() }
        .mapToPair { char -> Tuple2(char, 1) }
        .reduceByKey { a, b -> a + b }

    characters.foreach { tuple2 -> println("${tuple2._1} -> ${tuple2._2}") }
    sparkContext.stop()

}