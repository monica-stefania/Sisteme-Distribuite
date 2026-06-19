import java.text.SimpleDateFormat
import java.util.*

class Message private constructor(val sender: String, val body: String, val timestamp: Date, val name: String = "-", val telephone : String = "-", val email: String = "-") : Comparable<Message>{
    companion object {
        operator fun <T> List<T>.component6() = this[5]

        fun create(sender: String,body: String, name: String = "-", telephone: String = "-", email: String = "-"): Message {
            return Message(sender, body, Date(), name, telephone, email)
        }

        fun deserialize(msg: ByteArray): Message {
            val msgString = String(msg)
            val (timestamp, sender, name, telephone, email, body) = msgString.split("__~__", limit = 6)

            return Message(sender, body, Date(timestamp.toLong()), name, telephone, email)
        }
    }

    fun serialize(): ByteArray {
        //println("${timestamp.time}__~__${sender}__~__${name}__~__${telephone}__~__${email}__~__${body}\n")
        return "${timestamp.time}__~__${sender}__~__${name}__~__${telephone}__~__${email}__~__${body}\n".toByteArray()
    }

    override fun toString(): String {
        val dateString = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp)
        return "[$dateString] $sender {Name : $name, Telephone : $telephone, email : $email} >>> $body"
    }

    override fun compareTo(other: Message): Int = this.timestamp.compareTo(other.timestamp)
}

fun main(args: Array<String>) {
    val msg = Message.create("localhost:4848", "test mesaj")
    println(msg)
    val serialized = msg.serialize()
    val deserialized = Message.deserialize(serialized)
    println(deserialized)
}