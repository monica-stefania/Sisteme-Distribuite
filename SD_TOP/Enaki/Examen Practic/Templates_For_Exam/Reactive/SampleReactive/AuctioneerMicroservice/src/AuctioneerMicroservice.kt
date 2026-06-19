import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import logging.Logging
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.*
import kotlin.system.exitProcess


class AuctioneerMicroservice {
    private var auctioneerSocket: ServerSocket
    private var receiveBidsObservable: Observable<String>
    private val subscriptions = CompositeDisposable()
    private val bidQueue: Queue<Message> = LinkedList<Message>()
    private val bidderConnections: MutableList<Socket> = mutableListOf()

    companion object Constants {
        const val AUCTIONEER_PORT = 1500
        const val AUCTION_DURATION: Long = 15_000 // licitatia dureaza 15 secunde
        const val logPath = "auctioneer.log"
        val logger = Logging.instance
    }

    init {
        auctioneerSocket = ServerSocket(AUCTIONEER_PORT)
        auctioneerSocket.setSoTimeout(AUCTION_DURATION.toInt())
        println("AuctioneerMicroservice se executa pe portul: ${auctioneerSocket.localPort}")
        println("Se asteapta oferte de la bidderi...")
        logger.log(logPath, "AuctioneerMicroservice se executa pe portul: ${auctioneerSocket.localPort}")
        logger.log(logPath, "Se asteapta oferte de la bidderi...")


        receiveBidsObservable = Observable.create<String> { emitter ->
            while (true) {
                try {
                    val bidderConnection = auctioneerSocket.accept()
                    bidderConnections.add(bidderConnection)

                    val bufferReader = BufferedReader(InputStreamReader(bidderConnection.inputStream))
                    val receivedMessage = bufferReader.readLine()

                    if (receivedMessage == null) {
                        bufferReader.close()
                        bidderConnection.close()

                        emitter.onError(Exception("Eroare: Bidder-ul ${bidderConnection.port} a fost deconectat."))
                    }

                    emitter.onNext(receivedMessage)
                } catch (e: SocketTimeoutException) {
                    emitter.onComplete()
                    break
                }
            }
        }
    }

    private fun receiveBids() {
        val receiveBidsSubscription = receiveBidsObservable.subscribeBy(
            onNext = {
                val message = Message.deserialize(it.toByteArray())
                println(message)
                bidQueue.add(message)
            },
            onComplete = {
                println("Licitatia s-a incheiat! Se trimit ofertele spre procesare...")
                logger.log(logPath, "Licitatia s-a incheiat! Se trimit ofertele spre procesare...")

                finishAuction()
            },
            onError = { println("Eroare: $it") }
        )
        subscriptions.add(receiveBidsSubscription)
    }

    private fun finishAuction() {
        try {
            val winner: Message = bidQueue.toList().maxBy {
                it.body.split(" ")[1].toInt()
            }
                ?: throw Exception("Nici un bidder")

            println("Castigatorul este: ${winner?.sender} (Nume : ${winner?.name})")
            logger.log(logPath, "Castigatorul este: ${winner?.sender} (Nume : ${winner?.name})")

            val winningPrice = winner!!.body.split(" ")[1].toInt()


            println("Am primit rezultatul licitatiei de la BiddingProcessor: ${winner.sender} a castigat cu pretul: $winningPrice")
            logger.log(logPath, "Am primit rezultatul licitatiei de la BiddingProcessor: ${winner.sender} a castigat cu pretul: $winningPrice")

            // se creeaza mesajele pentru rezultatele licitatiei
            val winningMessage = Message.create(auctioneerSocket.localSocketAddress.toString(),
                "Licitatie castigata! Pret castigator: $winningPrice")
            val losingMessage = Message.create(auctioneerSocket.localSocketAddress.toString(),
                "Licitatie pierduta...")

            // se anunta castigatorul
            bidderConnections.forEach {
                if (it.remoteSocketAddress.toString() == winner.sender) {
                    it.getOutputStream().write(winningMessage.serialize())
                } else {
                    it.getOutputStream().write(losingMessage.serialize())
                }
                it.close()
            }
        } catch (e: Exception) {
            logger.log(logPath, "Eroare! Nici un bidder")

            auctioneerSocket.close()
            exitProcess(1)
        }

        subscriptions.dispose()
    }

    fun run() {
        receiveBids()
    }
}

fun main(args: Array<String>) {
    val bidderMicroservice = AuctioneerMicroservice()
    bidderMicroservice.run()
}