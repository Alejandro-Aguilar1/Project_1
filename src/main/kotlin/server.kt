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
                val userInput = reader.readLine()
                // val result = sequenceCheck(userInput)
                writer.write("$userInput\n")
                writer.flush()
            }
        }
        client.close()
    }
}