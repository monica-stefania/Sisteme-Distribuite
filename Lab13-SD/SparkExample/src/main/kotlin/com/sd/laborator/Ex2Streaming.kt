package com.sd.laborator

import org.apache.spark.SparkConf
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext

fun main() {

    val sparkConf = SparkConf()
        .setMaster("local[2]")
        .setAppName("Histograma Streaming")

    val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(5))

    val lines = streamingContext.textFileStream("src/main/resources/streaming_input/")

    // Explodam in caractere si filtram literele
    val chars = lines.flatMap { line ->
        line.toList()
            .filter { it.isLetter() }
            .map { it.lowercaseChar().toString() }
            .iterator()
    }

    // countByValue() numara automat aparitiile fiecarui caracter
    val histograma = chars.countByValue()

    // Afisam rezultatul fiecarui batch
    histograma.print(26)

    streamingContext.start()
    streamingContext.awaitTermination()
}