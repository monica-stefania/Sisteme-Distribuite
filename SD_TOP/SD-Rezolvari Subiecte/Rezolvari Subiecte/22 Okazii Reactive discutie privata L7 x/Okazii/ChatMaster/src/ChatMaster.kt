import Message.Companion.create
import Message.Companion.deserialize
import com.sun.xml.internal.ws.streaming.XMLStreamWriterUtil.getOutputStream
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.util.*
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class Observed(message_1: String, socket_1: Socket){
    var message: String = message_1
}
class ChatMaster {
    private var masterServerSocket: ServerSocket
    private var receiveMessagesObservable: Observable<Observed>
    private val messageQueue: Queue<Observed> = LinkedList<Observed>()
    private val clientConnections: MutableList<Socket> = mutableListOf()

    companion object Constants {
        const val MASTER_PORT = 1790
        //const val AUCTION_DURATION: Long = 15_000 // licitatia dureaza 15 secunde
    }

    init {
        masterServerSocket = ServerSocket(MASTER_PORT)
        //auctioneerSocket.setSoTimeout(AUCTION_DURATION.toInt())
        println("ChatMaster se executa pe portul: ${masterServerSocket.localPort}")
        println("Se asteapta mesaje de la clienti...")

        // se creeaza obiectul Observable cu care se genereaza evenimente cand se primesc mesaje de la clienti
        receiveMessagesObservable = Observable.create<Observed> { emitter ->
            // se asteapta conexiuni din partea clientilor
            while (true) {

                val clientConnection = masterServerSocket.accept()

                thread {
                    clientConnections.add(clientConnection)
                    println("Am pus in clientConnections: $clientConnection")
                    // se citeste mesajul de la client de pe socketul TCP

                    val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                    while(true) {
                        val receivedMessage = bufferReader.readLine()

                        // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                        if (receivedMessage == null) {
                            // deci subscriber-ul respectiv a fost deconectat
                            bufferReader.close()
                            clientConnection.close()
                            emitter.onError(Exception("Eroare: Clientul ${clientConnection.port} a fost deconectat."))
                        }

                        // se emite ce s-a citit ca si element in fluxul de mesaje
                        if (receivedMessage != "end of transmission")
                            emitter.onNext(Observed(receivedMessage, clientConnection))
                        else {
                            emitter.onComplete()
                            break
                        }
                    }
                }
            }
        }
    }

    private fun receiveMessages() {
        // se incepe prin a primi mesajele de la clienti
        val receiveMessagesSubscription = receiveMessagesObservable.subscribeBy(
            onNext = { received ->
                val message = Message.deserialize(received.message.toByteArray())
                println("PRIMIT:$message")
                messageQueue.add(received)
                clientConnections.forEach { connection ->
                        connection.getOutputStream().write((message.toString()+"\n").toByteArray())
                }
            },
            onComplete = {
                // conversatia s-a incheiat
                // se trimit raspunsurile mai departe catre procesorul de mesaje
                println("Conversatia s-a incheiat! ...")
            },
            onError = { println("Eroare: $it") }
        )
    }
    fun run() {
        thread {
            receiveMessages()
        }
    }
}
fun main(args: Array<String>) {
    val chatMicroservice = ChatMaster()
    chatMicroservice.run()
}