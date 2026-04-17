import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket
import java.util.UUID
import kotlin.Exception
import kotlin.random.Random
import kotlin.system.exitProcess

class BidderMicroservice() {
    private var auctioneerSocket: Socket
    private var auctionResultObservable: Observable<String>
    private var myIdentity: String = "[BIDDER_NECONECTAT]"
    private var bidderData: BidderData
    private var logger: ExecutionLogger


    companion object Constants {
        const val AUCTIONEER_HOST = "auctioneer"
        const val AUCTIONEER_PORT = 1500
        const val MAX_BID = 10_000
        const val MIN_BID = 1_000
        const val HEARTBEAT_HOST = "heartbeat"
        const val HEARTBEAT_PORT = 1800
    }

    init {
        try {
            startHeartbeat()
            auctioneerSocket = Socket(AUCTIONEER_HOST, AUCTIONEER_PORT)
            println("M-am conectat la Auctioneer!")

            //orfelitantii primesc un nume, nr de telefon si email random
            bidderData = randomBidderData()

            //aceasta va fi noua lor identitate
            myIdentity = "[${bidderData.name} - ${bidderData.phone} - ${bidderData.email}]"

            //generez un id random
            val bidderId = UUID.randomUUID().toString()

            logger = ExecutionLogger("bidder_$bidderId.log")

            logger.log("START")

            // se creeaza un obiect Observable ce va emite mesaje primite printr-un TCP
            // fiecare mesaj primit reprezinta un element al fluxului de date reactiv
            auctionResultObservable = Observable.create<String> { emitter ->
                // se citeste raspunsul de pe socketul TCP
                val bufferReader = BufferedReader(InputStreamReader(auctioneerSocket.inputStream))
                val receivedMessage = bufferReader.readLine()

                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                if (receivedMessage == null) {
                    bufferReader.close()
                    auctioneerSocket.close()

                    emitter.onError(Exception("AuctioneerMicroservice s-a deconectat."))

                    return@create
                }

                // mesajul primit este emis in flux
                emitter.onNext(receivedMessage)

                // deoarece se asteapta un singur mesaj, in continuare se emite semnalul de incheiere al fluxului
                emitter.onComplete()

                bufferReader.close()
                auctioneerSocket.close()
            }
        } catch (e: Exception) {
            println("$myIdentity Nu ma pot conecta la Auctioneer!")
            //logger.log("ERROR")
            exitProcess(1)
        }
    }

    private fun bid() {
        // se genereaza o oferta aleatorie din partea bidderului curent
        val pret = Random.nextInt(MIN_BID, MAX_BID)

        // se creeaza mesajul care incapsuleaza oferta
        val biddingMessage = Message.create("${auctioneerSocket.localAddress}:${auctioneerSocket.localPort}",
            "licitez $pret", bidderData.name, bidderData.phone, bidderData.email)

        // bidder-ul trimite pretul pentru care doreste sa liciteze
        val serializedMessage = biddingMessage.serialize()
        auctioneerSocket.getOutputStream().write(serializedMessage)
       // auctioneerSocket.getOutputStream().write(serializedMessage)

        logger.log("SEND_BID|${String(biddingMessage.serialize()).trim()}")

        // exista o sansa din 2 ca bidder-ul sa-si trimita oferta de 2 ori, eronat
        if (Random.nextBoolean()) {
            auctioneerSocket.getOutputStream().write(serializedMessage)
            logger.log("SEND_DUPLICATE_BID|${String(serializedMessage).trim()}")
        }
    }

    private fun waitForResult() {
        println("$myIdentity Astept rezultatul licitatiei...")
        logger.log("WAIT_RESULT")

        // bidder-ul se inscrie pentru primirea unui raspuns la oferta trimisa//logger.log("AUCTIONEER DECONECTAT [${auctioneerSocket.localPort} $myIdentity]") de acesta
        val auctionResultSubscription = auctionResultObservable.subscribeBy(
            // cand se primeste un mesaj in flux, inseamna ca a sosit rezultatul licitatiei
            onNext = {
                val resultMessage: Message = Message.deserialize(it.toByteArray())
                println("$myIdentity Rezultat licitatie: ${resultMessage.body}")
                logger.log("RESULT|${String(resultMessage.serialize()).trim()}")
                logger.log("FINAL")
                logger.clearAll()
            },
            onError = {
                println("$myIdentity Eroare: $it")
            }
        )

        // se elibereaza memoria obiectului Subscription
        auctionResultSubscription.dispose()
    }

    private fun startHeartbeat()
    {
        Thread {
            while(true){
                try{
                    val heartBeatSocket = Socket(HEARTBEAT_HOST, HEARTBEAT_PORT)
                    val containerName = System.getenv("HOSTNAME")
                    val message = Message.create("${heartBeatSocket.localAddress}:${heartBeatSocket.localPort}", "heartbeat bidder_${containerName}")

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
        val lines = logger.readAll().map {
            it.substringAfter("] ").trim()
        }
        val wait = lines.any { it.startsWith("WAIT_RESULT")}
        if(wait)
        {
            println("RECOVERY: astept rezultatul din nou")

            val oldBid = lines.find { it.startsWith(("SEND_BID")) }
            if(oldBid != null)
            {
                val oldOffer = oldBid.substringAfter("SEND_BID")
                println("Retrimit oferta veche")
                auctioneerSocket.getOutputStream().write((oldOffer + "\n").toByteArray())
            }
            waitForResult()
        }
        else {
            bid()
            waitForResult()
        }
    }
}

fun main(args: Array<String>) {
    val bidderMicroservice = BidderMicroservice()
    bidderMicroservice.run()
}