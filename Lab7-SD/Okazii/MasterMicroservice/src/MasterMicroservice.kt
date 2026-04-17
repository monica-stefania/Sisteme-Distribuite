import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket

class MasterMicroservice {
    private val masterSocket: ServerSocket = ServerSocket(MASTER_PORT)
    private val masterFile = File("/app/logs/master.log").also {
        it.parentFile.mkdirs()
    }
    private val mutex = Mutex()
    companion object
    {
        const val MASTER_PORT = 1900
    }

    suspend fun run() = coroutineScope {
        println("MasterMicroservice pornit pe portul $MASTER_PORT")
        while (true){
            val connection = masterSocket.accept()
            println("Conexiune noua primita de la: ${connection.inetAddress}")
            //pt fiecare conexiune lansez o corutina separata
            val bufferReader = BufferedReader(InputStreamReader(connection.inputStream))
            val receivedMessage = bufferReader.readLine()
            println("Mesaj citit: $receivedMessage")

            bufferReader.close()
            connection.close()

            if (receivedMessage != null) {
                println("Mesaj primit: $receivedMessage")
                mutex.withLock {
                    masterFile.appendText(receivedMessage + "\n")
                }
            }
        }
    }
}

fun main() = runBlocking {
    MasterMicroservice().run()
}