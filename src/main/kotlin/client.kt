import java.net.Socket
import java.util.Scanner

fun main(){
    val scanner = Scanner(System.`in`)
    println("Enter input string, a sequence of T's, A's, and M's (end with '#')")
    val tamTabClient = scanner.nextLine()

    Socket("localhost", 9999).use { socket ->
        val writer = socket.outputStream.bufferedWriter()
        writer.write("$tamTabClient\n")
        writer.flush()
        socket.getInputStream().bufferedReader().use { reader ->
            println("Input contains sequence: ${reader.readLine()}")
        }
    }
}

// $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")