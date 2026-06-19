package org.example

import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.JsonSerializer
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArraySerializer
import kotlinx.serialization.json.JsonElementSerializer
import kotlinx.serialization.json.JsonObjectSerializer
import kotlinx.serialization.parse
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.*
import scala.collection.parallel.ParIterableLike
import java.io.File

@UnstableDefault
fun main() {
    val negative_words = File("/home/stefan/Documents/Rezolvari Subiecte/23 CompanyNews PyTCP + KtSpark positive-negative x/KotlinStockMarketSpark/opinion-lexicon-English/negative-words.txt")
    val positive_words = File("/home/stefan/Documents/Rezolvari Subiecte/23 CompanyNews PyTCP + KtSpark positive-negative x/KotlinStockMarketSpark/opinion-lexicon-English/positive-words.txt")


    // configurarea Spark
    val sparkConf = SparkConf().setMaster("local[2]").setAppName("Spark StockMarket")
    // initializarea contextului Spark
    val sparkContext = JavaStreamingContext(sparkConf, Durations.seconds(5))
    val items = sparkContext.socketTextStream("localhost", 9998)

    val processed = items
        .map{ obj -> {
            val objString = obj.toString()
            Json.parse(JsonObjectSerializer, objString)
            }
        }
        .flatMap {
            val objJson = it()

            val array: Array<String> = arrayOf("1","2","3","4")

            val url: String = objJson["url"].toString()
            val datetime: String = objJson["datetime"].toString()
            val headline: String = objJson["headline"].toString()

            var pos = 0
            positive_words.forEachLine {
                if(objJson["summary"].toString().contains(it))
                    pos++
            }

            var neg = 0
            negative_words.forEachLine {
                if(objJson["summary"].toString().contains(it))
                    neg++
            }

            array[0] = url + "\n"
            array[1] = datetime + "\n"
            array[2] = headline + "\n"
            if(pos<neg)
                array[3] = "negative"
            else if(pos>neg)
                    array[3] = "positive"
            else array[3] = "neutral"

            array.iterator()
        }
//        .map{
//            val objJson = it()
//            "URL: " + objJson["url"].toString() + "\n" + "DATE: " + objJson["datetime"].toString() + "\n" + "TITLE: " + objJson["headline"].toString() + "\n"
//        }

    processed.print()

    sparkContext.start()
    sparkContext.awaitTermination()
}

