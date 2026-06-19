import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket

class NumberOfMessagesMicroservice {
    private var serverSocket: ServerSocket
    private var receiveObservable: Observable<String>
    private val subscriptions = CompositeDisposable()
    private var count = 0

    companion object Constants {
        const val PROCESSOR_PORT = 1650
    }
    init {
        serverSocket = ServerSocket(PROCESSOR_PORT)
        serverSocket.setSoTimeout(40_000)
        println("NumberOfMessagesMicroservice se executa pe portul: ${serverSocket.localPort}")
        println("Se asteapta celelalte servicii sa se conecteze...")

        receiveObservable = Observable.create<String> { emitter ->
            while (true) {
                try {
                    val clientConnection = serverSocket.accept()  // local, nu class field
                    val bufferReader = BufferedReader(InputStreamReader(clientConnection.inputStream))

                    // fiecare conexiune e citita pe un thread separat
                    Thread {
                        try {
                            while (true) {
                                val receivedMessage = bufferReader.readLine()

                                if (receivedMessage == null) {
                                    // conexiunea s-a inchis normal - nu e eroare fatala
                                    bufferReader.close()
                                    clientConnection.close()
                                    break  // iesim doar din acest thread, NU emitter.onError()
                                }

                                val message = Message.deserialize(receivedMessage.toByteArray())

                                if (message.body == "final") {
                                    emitter.onComplete()  // licitatia s-a terminat
                                    bufferReader.close()
                                    clientConnection.close()
                                    serverSocket.close()
                                    return@Thread
                                } else {
                                    emitter.onNext(receivedMessage)
                                }
                            }
                        } catch (e: Exception) {
                            println("Conexiune inchisa: ${e.message}")
                        }
                    }.start()

                } catch (e: Exception) {
                    // serverSocket.accept() a expirat (SoTimeout) sau serverSocket e inchis
                    if (serverSocket.isClosed) break
                    println("Accept timeout sau eroare: ${e.message}")
                }
            }
        }
    }

    fun receiveAndCountMessages() {
        val receiveSubscription = receiveObservable.subscribeBy(
            onNext = {
                // Incrementam pentru FIECARE mesaj care a fost necesar in cadrul fluxului
                count++
                println("Mesaj necesar licitatiei inregistrat. Total curent: $count")
            },
            onComplete = {
                println("Licitatia s-a adjudecat! Total mesaje necesare: $count")

                // Scriem informatia direct in fisierul local
                File("statistica.txt").writeText("Numarul total de mesaje necesare pentru licitatie a fost $count\n")
                println("Fisierul 'statistica.txt' a fost actualizat cu succes.")

                serverSocket.close()
                subscriptions.dispose()
            },
            onError = { println("Eroare: $it") }
        )
        subscriptions.add(receiveSubscription)
    }

    fun run() {
        receiveAndCountMessages()
    }
}

fun main(args: Array<String>) {
    val service = NumberOfMessagesMicroservice()
    service.run()
}
