import java.text.SimpleDateFormat
import java.util.*

class Message private constructor(
    val sender: String,
    val body: String,
    val timestamp: Date,
    val name: String,
    val phone: String,
    val email: String
) {
    companion object {
        fun create(sender: String, body: String, name: String = "", phone: String = "", email: String = ""): Message {
            return Message(sender, body, Date(), name, phone, email)
        }

        fun deserialize(msg: ByteArray): Message {
            val msgString = String(msg).trim()

            // Folosim un delimitator sigur, diferit de spatiu
            // Parametrul limit=6 asigura ca daca body-ul are si el un "|", nu ne strica parsarea
            val parts = msgString.split("|", limit = 6)

            val timestamp = Date(parts[0].toLong())
            val sender = parts[1]
            val name = parts[2]
            val phone = parts[3]
            val email = parts[4]
            val body = parts[5]

            return Message(sender, body, timestamp, name, phone, email)
        }
    }

    fun serialize(): ByteArray {
        // Folosim "|" pentru a separa campurile in mod sigur
        return "${timestamp.time}|$sender|$name|$phone|$email|$body\n".toByteArray()
    }

    override fun toString(): String {
        val dateString = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp)
        return "[$dateString] $name [$phone | $email] >>> $body"
    }
}