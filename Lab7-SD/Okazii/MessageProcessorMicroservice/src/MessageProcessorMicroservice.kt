import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import kotlin.system.exitProcess

class MessageProcessorMicroservice {
    private var messageProcessorSocket: ServerSocket
    private lateinit var biddingProcessorSocket: Socket
    private var auctioneerConnection:Socket
    private var receiveInQueueObservable: Observable<String>
    private val subscriptions = CompositeDisposable()
    private val messageQueue: Queue<Message> = LinkedList<Message>()
    private val logger = ExecutionLogger("message_processor.log")

    companion object Constants {
        const val MESSAGE_PROCESSOR_PORT = 1600
        const val BIDDING_PROCESSOR_HOST = "bidding-processor"
        const val BIDDING_PROCESSOR_PORT = 1700
        const val HEARTBEAT_HOST = "heartbeat"
        const val HEARTBEAT_PORT = 1800
    }

    init {
        startHeartbeat()
        messageProcessorSocket = ServerSocket(MESSAGE_PROCESSOR_PORT)
        println("MessageProcessorMicroservice se executa pe portul: ${messageProcessorSocket.localPort}")
        println("Se asteapta mesaje pentru procesare...")

        logger.log("START")

        // se asteapta mesaje primite de la AuctioneerMicroservice
        auctioneerConnection = messageProcessorSocket.accept()
        val bufferReader = BufferedReader(InputStreamReader(auctioneerConnection.inputStream))

        // se creeaza obiectul Observable cu care se captureaza mesajele de la AuctioneerMicroservice
        receiveInQueueObservable = Observable.create<String> { emitter ->
            while (true) {
                // se citeste mesajul de la AuctioneerMicroservice de pe socketul TCP
                val receivedMessage = bufferReader.readLine()

                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                if (receivedMessage == null) {
                    // deci subscriber-ul respectiv a fost deconectat
                    bufferReader.close()
                    auctioneerConnection.close()

                    emitter.onError(Exception("Eroare: AuctioneerMicroservice ${auctioneerConnection.port} a fost deconectat."))
                    logger.log("ERROR: Auctioneer ${auctioneerConnection.port} a fost deconectat")
                    break
                }

                // daca mesajul este cel de incheiere a licitatiei (avand corpul "final"), atunci se emite semnalul Complete
                if (Message.deserialize(receivedMessage.toByteArray()).body == "final") {
                    emitter.onComplete()

                    break
                } else {
                    // se emite ce s-a citit ca si element in fluxul de mesaje
                    emitter.onNext(receivedMessage)
                }
            }
        }
    }

    private fun receiveAndProcessMessages() {
        // se primesc si se adauga in coada mesajele de la AuctioneerMicroservice
        val receiveInQueueSubscription = receiveInQueueObservable
            //filtrati duplicatele folosind operatorul de filtrare
            .map{Message.deserialize(it.toByteArray())}
            .distinct {it.sender}

            .subscribeBy(
                onNext = {
                    message -> println(message)
                    messageQueue.add(message)
                    logger.log("MESSAGE_RECEIVED|${String(message.serialize()).trim()}")
                },
                onComplete = {
                    // s-a incheiat primirea tuturor mesajelor
                    ///se ordoneaza in functie de data si ora cand mesajele au fost primite
                    val sortedMessage = messageQueue.sortedBy { it.timestamp }
                    messageQueue.clear()
                    messageQueue.addAll(sortedMessage)

                    // s-au primit toate mesajele de la AuctioneerMicroservice, i se trimite un mesaj pentru a semnala
                    // acest lucru
                    val finishedMessagesMessage = Message.create(
                        "${auctioneerConnection.localAddress}:${auctioneerConnection.localPort}", "am primit tot")
                    auctioneerConnection.getOutputStream().write(finishedMessagesMessage.serialize())
                    auctioneerConnection.close()

                    val sortedData = messageQueue.joinToString(",") { String(it.serialize()).trim()}

                    logger.log("MESSAGE_SORTED|$sortedData")
                    // se trimit mai departe mesajele procesate catre BiddingProcessor
                    sendProcessedMessages()
                },
                onError = { println("Eroare: $it")
                }
            )
        subscriptions.add(receiveInQueueSubscription)
    }

    private fun sendProcessedMessages() {
        try {
            biddingProcessorSocket = Socket(BIDDING_PROCESSOR_HOST, BIDDING_PROCESSOR_PORT)

            println("Trimit urmatoarele mesaje:")
            Observable.fromIterable(messageQueue).subscribeBy(
                onNext = {
                    println(it.toString())

                    // trimitere mesaje catre procesorul licitatiei, care decide rezultatul final
                    biddingProcessorSocket.getOutputStream().write(it.serialize())

                    logger.log("MESSAGE_SENT|${String(it.serialize()).trim()}")
                },
                onComplete = {
                    val noMoreMessages = Message.create("${biddingProcessorSocket.localAddress}:${biddingProcessorSocket.localPort}",
                        "final", "", "", "")
                    biddingProcessorSocket.getOutputStream().write(noMoreMessages.serialize())
                    biddingProcessorSocket.close()

                    logger.log("FINAL")
                    // se elibereaza memoria din multimea de Subscriptions
                    subscriptions.dispose()
                    logger.clearAll()
                }
            )
        } catch (e: Exception) {
            println("Nu ma pot conecta la BiddingProcessor!")
            logger.log("ERROR|Nu ma pt coencta la BiddingProcessor")
            messageProcessorSocket.close()
            exitProcess(1)
        }
    }

    private fun startHeartbeat()
    {
        Thread {
            while(true){
                try{
                    val heartBeatSocket = Socket(HEARTBEAT_HOST,HEARTBEAT_PORT)
                    val message = Message.create("${heartBeatSocket.localAddress}:${heartBeatSocket.localPort}", "heartbeat message-processor")

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
        val lines = logger.readAll() .map { it.substringAfter("] ").trim() }
        val sortedMessage= lines.lastOrNull { it.startsWith("MESSAGE_SORTED|") }
        val hasFinal = lines.any { it == "FINAL" }

        if (sortedMessage!= null && !hasFinal) {
            println("RECOVERY: recuperez mesajele")
            val linesMess = sortedMessage.substringAfter("MESSAGE_SORTED|")

            messageQueue.clear()

            if (linesMess.isNotBlank()) {
                linesMess.split(",").forEach {
                    messageQueue.add(Message.deserialize(it.toByteArray()))
                }
            }
            sendProcessedMessages()
        }
        else {
            receiveAndProcessMessages()
        }
    }
}

fun main(args: Array<String>) {
    val messageProcessorMicroservice = MessageProcessorMicroservice()
    messageProcessorMicroservice.run()
}