
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.streaming.Durations
import org.apache.spark.streaming.api.java.*
import java.text.SimpleDateFormat
import java.util.Date

@Serializable
data class News(
    val category: String,
    val datetime: Long,
    val headline: String,
    val id: Long,
    val image: String,
    val related: String,
    val source: String,
    val summary: String,
    val url: String
): java.io.Serializable

object JsonConfig {
    val parser = Json { ignoreUnknownKeys = true }
}


fun main()
{
    // configurarea Spark
    val sparkConf = SparkConf().setMaster("local[*]").setAppName("Spark Kotlin")
    // initializarea contextului de streaming
    val streamingContext = JavaStreamingContext(
        sparkConf, Durations.seconds(3)
    )

    val lines = streamingContext.socketTextStream("localhost", 5050)
    val newsStream = lines
        .filter { line -> line.isNotBlank() }
        .map { line -> JsonConfig.parser.decodeFromString<News>(line) }

    // TEMPORAR: afiseaza tot ce vine, inainte de filtre
    newsStream.foreachRDD { rdd ->
        if (!rdd.isEmpty()) {
            rdd.collect().forEach { news ->
                println(">>> Primit: source=${news.source}, summaryLen=${news.summary.length}, title=${news.headline}")
            }
        }
    }

    val filteredNews = newsStream
        .filter { news ->  news.source == "Yahoo" }
        .filter { news -> news.summary.length > 500 }

    filteredNews.foreachRDD { rdd ->
        if(!rdd.isEmpty())
        {
            rdd.collect().forEach { news ->
                val date = SimpleDateFormat("yyyy.MM.dd").format(Date(news.datetime * 1000L))
                println("Title: ${news.headline}")
                println("Date: $date")
                println("Url: ${news.url}")
            }
        }
    }
    // Pornirea aplicației de streaming
    streamingContext.start()
    streamingContext.awaitTermination()
}