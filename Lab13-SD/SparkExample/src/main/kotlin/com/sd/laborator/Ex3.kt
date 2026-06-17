package com.sd.laborator

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.JavaStreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies
import kotlin.Any
import kotlin.String

fun main()
{
    // 1. Parametrii de conectare la Kafka
    //    - de unde sa se conecteze (broker)
    //    - cum sa decodeze mesajele (String)
    //    - group.id = numele grupului de consumatori
    //      (mai multi consumatori din acelasi grup
    //       impart munca intre ei)
    val kafkaParams = mutableMapOf<String, Any>(
        "bootstrap.servers" to "localhost:9092",
        "key.deserializer" to StringDeserializer::class.java,
        "value.deserializer" to StringDeserializer::class.java,
        "group.id" to "words-group",
        "auto.offset.reset" to "latest",
        "enable.auto.commit" to false
    )

    // 2. Configurare Spark
    //    local[2] = minim 2 thread-uri:
    //    - unul primeste datele de la Kafka (receiver)
    //    - unul proceseaza datele
    val sparkConf = SparkConf()
        .setMaster("local[*, 6]")
        .setAppName("Top15Cuvinte")

    // 3. Contextul de Streaming cu batch de 5 secunde
    //    la fiecare 5 secunde proceseaza ce a primit
    val streamingContext = JavaStreamingContext(sparkConf, Durations.seconds(5))

    // 4. Ne abonam la topic-ul "words-topic"
    //    Direct Stream = Spark citeste direct din Kafka
    //    fara receiver intermediar (mai eficient)
    val stream = KafkaUtils.createDirectStream(
        streamingContext,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.Subscribe<String, String>(
            listOf("words-topic"), kafkaParams
        )
    )

    // 5. Din fiecare mesaj Kafka extragem doar valoarea
    //    (cuvantul trimis de Producer)
    //    Un ConsumerRecord contine: topic, partition,
    //    offset, key, VALUE <- asta ne intereseaza
    val words = stream.map { record -> record.value() }

    // 6. Numaram aparitiile fiecarui cuvant
    val wordCounts = words.countByValue()

    // 7. La fiecare batch afisam top 15
    wordCounts.foreachRDD { rdd ->
        if (!rdd.isEmpty) {
            // sortam descrescator dupa count (al doilea element din pereche)
            val top15 = rdd.collect() //collect() aduce tot RDD-ul pe driver ca lista
                .sortedByDescending { it._2 }
                .take(15)

            println("=== Top 15 cuvinte ===")
            top15.forEach { pair ->
                println("${pair._1} -> ${pair._2}")
            }
        }
    }

    // 8. Pornim streaming-ul si asteptam
    streamingContext.start()
    streamingContext.awaitTermination()
}