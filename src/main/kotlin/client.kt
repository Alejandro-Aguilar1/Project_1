import java.net.Socket
import java.util.Scanner

fun main(){
    val scanner = Scanner(System.`in`)
    println("Enter input string (end with '.'")
    val input = scanner.nextLine()

    Socket("localhost", 9999).use { socket ->
        val writer = socket.outputStream.bufferedWriter()
        writer.write("$input\n")
        writer.flush()
        // Make sure to not close the writer here

        socket.getInputStream().bufferedReader().use { reader ->
            println("Input contains sequence: ${reader.readLine()}")
        }
    }
}

// $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")