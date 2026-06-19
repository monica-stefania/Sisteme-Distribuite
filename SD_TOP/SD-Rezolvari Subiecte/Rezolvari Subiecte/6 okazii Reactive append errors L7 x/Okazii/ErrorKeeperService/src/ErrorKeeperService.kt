import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException

class ErrorKeeperService {
    private var serverSocket: ServerSocket
    private val serviceConnections: MutableList<Socket> = mutableListOf()

    companion object Constants {
        const val PROCESSOR_PORT = 1750
    }

    init {
        serverSocket = ServerSocket(PROCESSOR_PORT)
        serverSocket.setSoTimeout(30_000)
        println("NumberOfMessagesMicroservice se executa pe portul: ${serverSocket.localPort}")
        println("Se asteapta celelalte servicii sa se conecteze...")
        while (true) {
            try {
                val serviceConnection = serverSocket.accept()
                serviceConnections.add(serviceConnection)

                // se citeste mesajul de la service de pe socketul TCP
                val bufferReader = BufferedReader(InputStreamReader(serviceConnection.inputStream))
                val receivedMessage = bufferReader.readLine()

                // daca se primeste un mesaj gol (NULL), atunci inseamna ca cealalta parte a socket-ului a fost inchisa
                if (receivedMessage == null) {
                    // deci subscriber-ul respectiv a fost deconectat
                    bufferReader.close()
                    serviceConnection.close()
                    println("Eroare: Bidder-ul ${serviceConnection.port} a fost deconectat.")
                }
                else {
                    File("/home/stefan/Documents/Rezolvari Subiecte/6 okazii L7 de revenit/Okazii/statistici.txt").appendText(receivedMessage.toString() + '\n')
                }
            } catch (e: SocketTimeoutException) {
                // daca au trecut cele 20 secunde de la pornirea licitatiei, inseamna ca licitatia s-a incheiat
                // se emite semnalul Complete pentru a incheia fluxul de oferte
                println("finalul licitatiei")
                break
            }
        }
    }
}

fun main(args: Array<String>) {
    val errorKeeperMicroservice = ErrorKeeperService()
}