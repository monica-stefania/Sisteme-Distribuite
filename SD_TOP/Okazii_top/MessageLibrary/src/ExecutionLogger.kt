import java.io.File
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.Date

class ExecutionLogger(private val fileName: String) {
    private val file: File = File("/app/logs/$fileName").also {
        it.parentFile.mkdirs() // creaza folderul /app/logs/ daca nu exista
    }

    //private val masterSocket = ServerSocket(MASTER_HOST, MASTER_PORT)

    companion object {
        const val MASTER_PORT = 1900
        const val MASTER_HOST = "master"
    }

    //scriu mesajele in fisierul corespunzator microserviciului
    fun log(message: String){
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val timestamp = dateFormat.format(Date())
        val line = "[$timestamp] $message"
        file.appendText(line + "\n")

        try{
            val masterSocket = Socket(MASTER_HOST, MASTER_PORT)
            println("Trimit catre master: $line")
            val writer = masterSocket.getOutputStream().bufferedWriter()
            writer.write("[$timestamp] [$fileName] $message \n")
            writer.flush()
            masterSocket.close()
        }catch (e: Exception)
        {
            println("Nu se poate realiza conexiunea cu serverul Master")
        }
    }

    fun readAll(): List<String>{
        if (file.exists())
            return file.readLines()
        return emptyList()
    }
    fun clearAll(){
        if(file.exists())
            file.writeText("")
    }
}
