import kotlinx.coroutines.*
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.util.ArrayList

/**
 * Runs a server on [port] indefinitely, accepting clients and processing their requests.
 * This is a *suspend* function so you can safely call it from a coroutine (e.g., from a GUI).
 */

// Global (or in an object) variable to hold the current server socket
var currentServerSocket: ServerSocket? = null

suspend fun startServer(port: Int) = coroutineScope {
    withContext(Dispatchers.IO) {
        val server = ServerSocket().apply {
            reuseAddress = true
            bind(java.net.InetSocketAddress(port))
        }
        currentServerSocket = server
        println("Server listening on port $port")

        // In order to start and stop the server, we need to enclose the while state in a try catch block
        try {
            while (true) {
                // Accept is a blocking call, so it's okay inside withContext(Dispatchers.IO)
                val clientSocket: Socket = server.accept()
                println("Client connected: ${clientSocket.inetAddress.hostAddress}")

                // Each client can be handled in its own coroutine
                launch(Dispatchers.IO) {
                    handleClient(clientSocket)
                }
            }
        } catch (e: CancellationException) {
            println("Server cancelled: ${e.message}")
            throw e
        } catch (e: SocketException) {
            // This exception is expected when the server socket is closed.
            println("Server socket closed (SocketException): ${e.message}")
        } finally {
            try {
                server.close()
            } catch (e: Exception) {
                // Log an exception
            }
            // Ensure the server socket is closed whether or not an exception occurs
            println("Server closed")
            currentServerSocket = null
        }

    }
}

/**
 * Handle the request/response cycle for a single client.
 */
private suspend fun handleClient(socket: Socket) = withContext(Dispatchers.IO) {
    socket.use { client ->
        val reader = client.getInputStream().bufferedReader()
        val writer = client.getOutputStream().bufferedWriter()

        val userInput = reader.readLine().orEmpty()
        println("Server received: $userInput")

        // Filter only characters in "TAMtam"
        val filteredList = userInput.filter { it in "TAMtam" }.toList()

        writer.write("Original Input Array: $filteredList\n")
        writer.flush()

        val tamTabServer = sortTamServer(ArrayList(filteredList))
        writer.write("Sorted Input Array: $tamTabServer\n")
        writer.flush()
    }
}

//import java.net.ServerSocket
//
//fun main(){
//    val port = 9999
//    val server = ServerSocket(port)
//    println("Server Socket listening on port $port")
//
//    // We want to be able to keep the server going until the terminal is killed
//    while (true) {
//        val client = server.accept()
//        println("Client Socket connected: ${client.inetAddress.hostAddress}")
//
//        client.getInputStream().bufferedReader().use{ reader ->
//            client.getOutputStream().bufferedWriter().use{ writer ->
//                // We take the raw input of the user
//                val userInput: String = reader.readLine()
//                println("Received input: '$userInput'")
//
//                // We turn the raw input into an arrayList of characters
//                var tamTabServer = ArrayList(userInput.filter { it in "TAMtam" }.toList())
//                writer.write("Original Input Array ${tamTabServer}\n")
//                tamTabServer = sortTamServer(tamTabServer)
//                writer.write("Sorted Input Array ${tamTabServer}\n")
//                writer.flush()
//            }
//        }
//        client.close()
//    }
//}