// File: client.kt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Socket

suspend fun sendMessageExportable(message: String): List<String> {
    return withContext(Dispatchers.IO) {
        val TAM_TAB_Client = mutableListOf<String>()
        try {
            Socket("localhost", 9999).use { socket ->
                val writer = socket.getOutputStream().bufferedWriter()
                val reader = socket.getInputStream().bufferedReader()

                // Write the message to the server.
                writer.write("$message\n")
                writer.flush()

                // Read the response lines until the end of the stream.
                while (true) {
                    val line = reader.readLine() ?: break
                    TAM_TAB_Client.add(line)
                }
                //print(TAM_TAB_Client)
            }
        } catch (e: Exception) {
            TAM_TAB_Client.add("Error: ${e.message}")
        }
        TAM_TAB_Client
    }
}