import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

class AuctioneerMicroservice {
    private var auctioneerSocket: ServerSocket
    private lateinit var messageProcessorSocket: Socket
    private var receiveBidsObservable: Observable<String>
    private val subscriptions = CompositeDisposable()
    private val bidQueue: Queue<Message> = LinkedList<Message>()
    private val bidderConnections: MutableList<Socket> = mutableListOf()
    private val logger = ExecutionLogger("auctioneer.log")

    companion object Constants {
        const val MESSAGE_PROCESSOR_HOST = "message-processor"
        const val MESSAGE_PROCESSOR_PORT = 1600
        const val AUCTIONEER_PORT = 1500
        const val AUCTION_DURATION: Long = 15_000 // licitatia dureaza 15 secunde
        const val HEARTBEAT_HOST = "heartbeat"
        const val HEARTBEAT_PORT = 1800
    }

    init {
        startHeartbeat()
        auctioneerSocket = ServerSocket(AUCTIONEER_PORT)
        auctioneerSocket.setSoTimeout(AUCTION_DURATION.toInt())
        println("AuctioneerMicroservice se executa pe portul: ${auctioneerSocket.localPort}")
        println("Se asteapta oferte de la bidderi...")
        logger.log("START")

        // se creeaza obiectul Observable cu care se genereaza evenimente cand se primesc oferte de la bidderi
        receiveBidsObservable = Observable.create<String> { emitter ->
            // se asteapta conexiuni din partea bidderilor
            while (true) {
                try {
                    val bidderConnection = auctioneerSocket.accept()
                    bidderConnections.add(bidderConnection)

                    // se citeste mesajul de la bidder de pe socketul TCP
                    val bufferReader = BufferedReader(InputStreamReader(bidderConnection.inputStream))
                    val receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if (receivedMessage == null) {
                        // deci subscriber-ul respectiv a fost deconectat
                        bufferReader.close()
                        bidderConnection.close()

                        emitter.onError(Exception("Eroare: Bidder-ul ${bidderConnection.port} a fost deconectat."))
                        logger.log("ERROR|Bidder-ul ${bidderConnection.port} a fost deconectat")
                    }

                    // se emite ce s-a citit ca si element in fluxul de mesaje
                    emitter.onNext(receivedMessage)
                } catch (e: SocketTimeoutException) {
                    // daca au trecut cele 15 secunde de la pornirea licitatiei, inseamna ca licitatia s-a incheiat
                    // se emite semnalul Complete pentru a incheia fluxul de oferte
                    emitter.onComplete()
                    logger.log("TIME_ENDED|Au trecut cele 15 secunde")
                    break
                }
            }
        }
    }

    private fun receiveBids() {
        // se incepe prin a primi ofertele de la bidderi
        val receiveBidsSubscription = receiveBidsObservable.subscribeBy(
            onNext = {
                val message = Message.deserialize(it.toByteArray())
                println(message)
                bidQueue.add(message)
                logger.log("OFFER|${String(message.serialize()).trim()}")
            },
            onComplete = {
                // licitatia s-a incheiat
                // se trimit raspunsurile mai departe catre procesorul de mesaje
                println("Licitatia s-a incheiat! Se trimit ofertele spre procesare...")
                logger.log("END_RECEIVED")
                forwardBids()
            },
            onError = { println("Eroare: $it")
                logger.log("ERROR|Eroarea este $it")
            }
        )
        subscriptions.add(receiveBidsSubscription)
    }

    private fun forwardBids() {
        try {
            messageProcessorSocket = Socket(MESSAGE_PROCESSOR_HOST, MESSAGE_PROCESSOR_PORT)
            subscriptions.add(Observable.fromIterable(bidQueue).subscribeBy(
                onNext = {
                    // trimitere mesaje catre procesorul de mesaje
                    messageProcessorSocket.getOutputStream().write(it.serialize())
                    println("Am trimis mesajul: $it")
                    logger.log("SEND|${String(it.serialize()).trim()}")
                },
                onComplete = {
                    println("Am trimis toate ofertele catre MessageProcessor.")
                    logger.log("SEND_ALL|Au fost trimise toate ofertele")
                    val bidEndMessage = Message.create(
                        "${messageProcessorSocket.localAddress}:${messageProcessorSocket.localPort}",
                        "final", "", "", ""
                    )
                    messageProcessorSocket.getOutputStream().write(bidEndMessage.serialize())

                    // dupa ce s-a terminat licitatia, se asteapta raspuns de la MessageProcessorMicroservice
                    // cum ca a primit toate mesajele
                    val bufferReader = BufferedReader(InputStreamReader(messageProcessorSocket.inputStream))
                    bufferReader.readLine()

                    messageProcessorSocket.close()

                    finishAuction()
                }
            ))
        } catch (e: Exception) {
            println("Nu ma pot conecta la MessageProcessor!")
            auctioneerSocket.close()
            exitProcess(1)
        }
    }

    private fun finishAuction() {
        // se asteapta rezultatul licitatiei
        try {
            val biddingProcessorConnection = auctioneerSocket.accept()
            val bufferReader = BufferedReader(InputStreamReader(biddingProcessorConnection.inputStream))

            // se citeste rezultatul licitatiei de la AuctioneerMicroservice de pe socketul TCP
            val receivedMessage = bufferReader.readLine()

            val result: Message = Message.deserialize(receivedMessage.toByteArray())
            val winningPrice = result.body.split(" ")[1].toInt()
            println("Am primit rezultatul licitatiei de la BiddingProcessor: ${result.sender} ${result.name} a castigat cu pretul: $winningPrice")

            logger.log("WINNER|${String(result.serialize()).trim()}")

            // se creeaza mesajele pentru rezultatele licitatiei
            val winningMessage = Message.create(auctioneerSocket.localSocketAddress.toString(),
                "Licitatie castigata! Pret castigator: $winningPrice", result.name, result.phone, result.email)
            val losingMessage = Message.create(auctioneerSocket.localSocketAddress.toString(),
                "Licitatie pierduta...", "", "", "")

            // se anunta castigatorul
            bidderConnections.forEach {
                if (it.remoteSocketAddress.toString() == result.sender) {
                    it.getOutputStream().write(winningMessage.serialize())
                } else {
                    it.getOutputStream().write(losingMessage.serialize())
                }
                it.close()
            }
            logger.log("FINAL")
            logger.clearAll()

        } catch (e: Exception) {
            println("Nu ma pot conecta la BiddingProcessor!")
            auctioneerSocket.close()
            exitProcess(1)
        }

        // se elibereaza memoria din multimea de Subscriptions
        subscriptions.dispose()
    }

    private fun startHeartbeat()
    {
        Thread {
            while(true){
                try{
                    val heartBeatSocket = Socket(HEARTBEAT_HOST, HEARTBEAT_PORT)
                    val message = Message.create("${heartBeatSocket.localAddress}:${heartBeatSocket.localPort}", "heartbeat auctioneer")

                    heartBeatSocket.getOutputStream().write(message.serialize())
                    heartBeatSocket.close()
                }
                catch(e: Exception)
                {
                    println("Eroare! Nu pot trimite heartbeat: $e")
                }

                Thread.sleep(5000)
            }
        }.also { it.isDaemon = true }.start()
    }

    fun run() {
        //extrag toate liniile din fisier
        val lines = logger.readAll().map { it.substringAfter("] ").trim() }

        val hasReceivedEnd = lines.any { it.startsWith("END_RECEIVED")}
        val hasSendAll = lines.any { it.startsWith("SEND_ALL") }
        val hasWinner = lines.any { it.startsWith("WINNER|") }

        if (hasReceivedEnd) {
            println("RECOVERY: reconstruim mesajele si le trimitem din nou")
            //iau toate liniile care au OFFER
            val offerLines = lines.filter { it.startsWith("OFFER|") }.map { it.substringAfter("OFFER|") }
            //adaug mesajele in coada
            bidQueue.clear()
            for (string in offerLines) {
                bidQueue.add(Message.deserialize(string.toByteArray()))
            }
            forwardBids()

        } else if (hasSendAll && !hasWinner) {
            println("RECOVERY: se astepta castigatorul din nou")
            finishAuction()

        } else {
            receiveBids()
        }
    }
}

fun main(args: Array<String>) {
    val bidderMicroservice = AuctioneerMicroservice()
    bidderMicroservice.run()
}