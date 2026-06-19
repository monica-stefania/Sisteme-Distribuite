import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Thread.sleep
import java.net.Socket
import kotlin.Exception
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.system.exitProcess

class ChatClient {
    private var masterSocket: Socket
    private var messagesObservable: Observable<String>
    private var myIdentity: String = "[CLIENT_NECONECTAT]"

    companion object Constants {
        const val MASTER_HOST = "localhost"
        const val MASTER_PORT = 1790
    }

    init {
        try {
            masterSocket = Socket(MASTER_HOST, MASTER_PORT)
            println("M-am conectat la Master!")

            myIdentity = "[${masterSocket.localPort}]"

            // se creeaza un obiect Observable ce va emite mesaje primite printr-un TCP
            // fiecare mesaj primit reprezinta un element al fluxului de date reactiv
            messagesObservable = Observable.create<String> { emitter ->
                while (true) {
                    println(masterSocket)
                    val bufferReader = BufferedReader(InputStreamReader(masterSocket.inputStream))

                    val receivedMessage = bufferReader.readLine()

                    // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                    if (receivedMessage == null) {
                        bufferReader.close()
                        masterSocket.close()

                        emitter.onError(Exception("MasterMicroservice s-a deconectat."))
                        return@create
                    }

                    // mesajul primit este emis in flux
                    emitter.onNext(receivedMessage)

//                    bufferReader.close()
//                    masterSocket.close()
                }
            }
        } catch (e: Exception) {
            println("$myIdentity Nu ma pot conecta la Master!")
            exitProcess(1)
        }
    }

    private fun sendMessage(message: String) {
        // se creeaza mesajul care incapsuleaza oferta
        val randomMessageObject = Message.create(
            "${masterSocket.localAddress}:${masterSocket.localPort}",
            message
        )

        val serializedMessage = randomMessageObject.serialize()
        masterSocket.getOutputStream().write(serializedMessage)
    }

    private fun waitForResponse() {
        println("$myIdentity Astept mesaje de la master...")
        val auctionResultSubscription = messagesObservable.subscribeBy(
            onNext = {
                println("$myIdentity Raspuns de la ceilalti: $it")
            },
            onError = {
                println("$myIdentity Eroare: $it")
            }
        )

        // se elibereaza memoria obiectului Subscription
        auctionResultSubscription.dispose()
    }

    fun run() {
        thread { waitForResponse() }
        for (i in 0..6) {
            val randomMessage = Random.nextInt(0, 1000)
            sleep(3000)
            sendMessage(randomMessage.toString())
        }
        sleep(3000)
        masterSocket.getOutputStream().write("end of transmission".toByteArray())
    }
}

fun main(args: Array<String>) {
    val chatClient = ChatClient()
    chatClient.run()
}