import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.util.HalfSerializer.onNext
import io.reactivex.rxjava3.kotlin.subscribeBy
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.exitProcess

class HeartBeatMicroservice{
    private var heartBeatSocket: ServerSocket
    private var receiveHeartBeasObservable: Observable<String>
    private var logger = ExecutionLogger("heartbeat.log")
    private var subscriptions = CompositeDisposable()

    // retine ultima data cand a primit heartbeat de la fiecare serviciu
    // cheia e numele containerului, valoarea e timestamp-ul ultimului heartbeat
    private val lastHeartbeat = ConcurrentHashMap<String, Long>()

    companion object Constants {
        const val HEARTBEAT_PORT = 1800
        const val HEARTBEAT_TIMEOUT = 10_000L

    }

    init {
        try {
            heartBeatSocket = ServerSocket(HEARTBEAT_PORT)
            println("HeartBeatMicroservice se executa pe portul ${heartBeatSocket.localPort}")
            println("Se asteapta heartbeat-uri...")
            logger.log("START")

            receiveHeartBeasObservable = Observable.create<String> { emitter ->
                while(true)
                {
                    var serviceConnection: Socket? = null
                    try{
                        serviceConnection = heartBeatSocket.accept()
                        val bufferReader = BufferedReader(InputStreamReader(serviceConnection.inputStream))

                        val receivedMessage = bufferReader.readLine()

                        //daca mesajul este null inseamna ca microserviciul a fost deconectat
                        if(receivedMessage == null)
                        {
                            bufferReader.close()
                            serviceConnection.close()

                            emitter.onError(Exception("Eroare: Microserviciul ${serviceConnection.port} a fost deconectat."))
                            logger.log("ERROR: Microserviciul ${serviceConnection.port} a fost deconectat")
                            break
                        }

                        emitter.onNext(receivedMessage)
                        bufferReader.close()
                        serviceConnection.close()
                    }
                    catch (e: Exception)
                    {
                        emitter.onError(e)
                        serviceConnection?.close()
                    }
                }
            }
        } catch (e: Exception)
        {
            println("Nu pot porni SupervisorMicroservice!")
            logger.log("ERROR|${e.message}")
            exitProcess(1)
        }
    }

    private fun receiveHeartbeats()
    {
        val receiveHeartbeatSubscription = receiveHeartBeasObservable.subscribeBy(
            onNext = {
                val message = Message.deserialize(it.toByteArray())
                val containerName = message.body.split(" ")[1]
                lastHeartbeat[containerName] = System.currentTimeMillis()
                println("Heartbeat primit de la: $containerName")
                logger.log("HEARTBEAT|$containerName")
            },
            onError = {
                println("Eroare: $it")
                logger.log("EROARE|$it")
            }
        )
        subscriptions.add(receiveHeartbeatSubscription)
    }

    // ruleaza intr-un thread separat si verifica periodic daca serviciile sunt vii
    private fun monitorServices() {
        Thread {
            while (true) {
                Thread.sleep(3000)
                val now = System.currentTimeMillis()

                lastHeartbeat.forEach { (containerName, lastTime) ->
                    if (now - lastTime > HEARTBEAT_TIMEOUT) {
                        println("$containerName nu a trimis heartbeat!")
                        logger.log("RESTART|$containerName")
                        val dockerId = containerName.removePrefix("bidder_")

                        //repornim containerul prin Docker
                        try {
                            val process = Runtime.getRuntime().exec(arrayOf("docker", "restart", dockerId))
                            val exitCode = process.waitFor()

                            if(exitCode == 0)
                                println("Restart executat pentru $containerName")
                            else{
                                val errorOutput = process.errorStream.bufferedReader().readText()
                                println("Comanda Docker a esuat (Cod $exitCode). Motiv: $errorOutput")
                            }
                        }catch (e: Exception)
                        {
                            println("Eroare la executia comenzii Docker: ${e.message}")
                        }
                    }
                }
            }
        }.start()
    }

    fun run() {
        monitorServices() // porneste monitorizarea in background
        receiveHeartbeats() // asculta heartbeat-uri
    }
}

fun main(args: Array<String>)
{
    HeartBeatMicroservice().run()
}
