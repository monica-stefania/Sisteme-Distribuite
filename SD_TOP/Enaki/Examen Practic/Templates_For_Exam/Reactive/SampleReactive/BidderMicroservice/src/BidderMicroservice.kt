import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import kotlin.Exception
import kotlin.random.Random
import kotlin.system.exitProcess

class BidderMicroservice {
    private var auctioneerSocket: Socket
    private var auctionResultObservable: Observable<String>
    private var myIdentity: String = "[BIDDER_NECONECTAT]"

    companion object Constants {
        const val AUCTIONEER_HOST = "localhost"
        const val AUCTIONEER_PORT = 1500
        const val MAX_BID = 10_000
        const val MIN_BID = 1_000
        val names = listOf(
                    "Edith Arispe",
                    "Barbra Bernat",
                    "Ashley Ricco",
                    "Alan Kluth",
                    "Brittny Shirkey",
                    "Aimee Ashcraft",
                    "Aleen Hatter",
                    "Santina Wolfgang",
                    "Kyung Reys",
                    "Claris Regnier",
                    "Cordell Geary",
                    "Pamala Yelle",
                    "Dung Garlow",
                    "Kari Norsworthy",
                    "Dalton Reale",
                    "Annemarie Wakeman",
                    "Adelle Depaul",
                    "Sherry Kiley",
                    "Shaquita Stemple",
                    "Benita Hair",
                    "Faye Brevard",
                    "Merry Calcote",
                    "Marylyn Woodworth",
                    "Tommye Zeledon",
                    "Barry Mulligan",
                    "Jacinda Perlmutter",
                    "Miesha Lusby",
                    "Janita Wild",
                    "Cruz Strahan",
                    "Garth Konrad",
                    "Malinda Engelhard",
                    "Araceli Fulop",
                    "Rita Anguiano",
                    "Carrie Tell",
                    "Augustina Hass",
                    "Lawana Wongus",
                    "Tillie Segarra",
                    "Ara Samet",
                    "Theola Garmon",
                    "Dino Parras",
                    "Lemuel Dorazio",
                    "Johnsie Race",
                    "Sydney Dam",
                    "Anastasia Tercero",
                    "Dortha Ellenburg",
                    "Deneen Notter",
                    "Arla Alban",
                    "Brande Looper",
                    "Jolynn Marlin",
                    "Suzann Chiaramonte")

        val mailServices = listOf("gmail.com", "yahoo.com", "mail.com", "aol.com", "student.tuiasi.ro")
    }

    init {
        try {
            auctioneerSocket = Socket(AUCTIONEER_HOST, AUCTIONEER_PORT)
            println("M-am conectat la Auctioneer!")

            myIdentity = "[${auctioneerSocket.localPort}]"

            auctionResultObservable = Observable.create<String> { emitter ->
                val bufferReader = BufferedReader(InputStreamReader(auctioneerSocket.inputStream))
                val receivedMessage = bufferReader.readLine()

                if (receivedMessage == null) {
                    bufferReader.close()
                    auctioneerSocket.close()

                    emitter.onError(Exception("AuctioneerMicroservice s-a deconectat."))
                    return@create
                }

                emitter.onNext(receivedMessage)

                emitter.onComplete()

                bufferReader.close()
                auctioneerSocket.close()
            }
        } catch (e: Exception) {
            println("$myIdentity Nu ma pot conecta la Auctioneer!")
            exitProcess(1)
        }
    }

    private fun bid() {
        val pret = Random.nextInt(MIN_BID, MAX_BID)

        val name = names[Random.nextInt(0, names.size - 1)]
        val email = name.replace(' ', '.').toLowerCase() + "@" + mailServices[Random.nextInt(0, mailServices.size)]

        val biddingMessage = Message.create("${auctioneerSocket.localAddress}:${auctioneerSocket.localPort}",
            "licitez $pret", name, "074" + Random.nextInt(1_000_000, 9_999_999), email)

        val serializedMessage = biddingMessage.serialize()
        auctioneerSocket.getOutputStream().write(serializedMessage)

        if (Random.nextBoolean()) {
            auctioneerSocket.getOutputStream().write(serializedMessage)
        }
    }

    private fun waitForResult() {
        println("$myIdentity Astept rezultatul licitatiei...")
        val auctionResultSubscription = auctionResultObservable.subscribeBy(
            onNext = {
                val resultMessage: Message = Message.deserialize(it.toByteArray())
                println("$myIdentity Rezultat licitatie: ${resultMessage.body}")
            },
            onError = {
                println("$myIdentity Eroare: $it")
            }
        )

        auctionResultSubscription.dispose()
    }

    fun run() {
        bid()
        waitForResult()
    }
}

fun main(args: Array<String>) {
    val bidderMicroservice = BidderMicroservice()
    bidderMicroservice.run()
}