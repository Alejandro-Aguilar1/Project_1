import java.net.Socket
import java.util.Scanner

fun main(){
    val scanner = Scanner(System.`in`)
    println("Enter input string, a sequence of T's, A's, and M's (end with '#')")

    val inputBuilder = StringBuilder()
    while (true) {
        val line = scanner.nextLine()
        inputBuilder.append(line)
        if (line.last() == '#') break
    }

    val tamTabClient = inputBuilder.dropLast(1)

    Socket("localhost", 9999).use { socket ->
        val writer = socket.outputStream.bufferedWriter()
        writer.write("$tamTabClient\n")
        println("$tamTabClient")
        writer.flush()

        socket.getInputStream().bufferedReader().use { reader ->
            //println("Input contains sequence: ${reader.readLine()}")
            reader.forEachLine { line ->
                println(line)
            }
        }
    }
}
