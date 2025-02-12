import java.io.*
import java.net.ServerSocket

fun main(){
    val port = 9999
    val server = ServerSocket(port)
    println("Server Socket listening on port $port")

    // We want to be able to keep the server going until the terminal is killed
    while (true) {
        val client = server.accept()
        println("Client Socket connected: ${client.inetAddress.hostAddress}")

        client.getInputStream().bufferedReader().use{ reader ->
            client.getOutputStream().bufferedWriter().use{ writer ->
                // We take the raw input of the user
                val userInput: String = reader.readLine()

                // We turn the raw input into an arrayList of characters
                var tamTabClient: ArrayList<Char> = ArrayList(userInput.toList())
                writer.write("Original Input Array ${tamTabClient}\n")
                tamTabClient = sortTamServer(tamTabClient)
                writer.write("Sorted Input Array ${tamTabClient}\n")
                writer.flush()
            }
        }
        client.close()
    }
}