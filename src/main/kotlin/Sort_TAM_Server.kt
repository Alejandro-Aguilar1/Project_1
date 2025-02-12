fun sortTamServer(arr: ArrayList<Char>): ArrayList<Char> {
    /*
    Given some array with Ts, As, and Ms, sort the array so that
    the left contain all the Ts, the right contains all the Ms; and
    the As are in between these Ts and Ms.

    - Constraint 1: Each letter (‘A’, ‘M’, or ‘T’) is evaluated only once.
    - Constraint 2: The function SWAP_Server(TAM_TAB_Server, i, j) is used only when it
    is necessary.
    - Constraint 3: No extra space can be used by the algorithm Sort_TAM_Server. In other
    words, only the array TAM_TAB_Server can be used to sort the ‘A’, ‘M’, or ‘T’.
    - Constraint 4: The algorithm, Sort_TAM_Server, cannot count the number of each letter
    ‘A’, ‘M’, or ‘T’ in TAM_TAB_Server.
     */

    var low: Int = 0
    var high: Int = arr.size - 1
    var mid: Int = 0
    while (mid <= high) {
        when (arr[mid]) {
            'T' -> {
                swapServer(arr, low, mid)
                mid ++
                low ++
            }
            'A' -> mid ++
            else -> {
                swapServer(arr, mid, high)
                high -= 1
            }
        }
    }
    return arr
}

/*
We have a function at the server side, called
SWAP_Server(TAM_TAB_Server, i, j), which places the ith letter in the jth entry
and the jth letter in the ith entry of TAM_TAB_Server. Note that SWAP_Server(TAM_TAB_Server, i, j) is defined for
all integers i and j between 0 and length(TAM_TAB_Server) – 1, where length(TAM_TAB_Server)
is the number of letters of the input string TAM.
 */
fun swapServer(arr: ArrayList<Char>, i: Int, j: Int ) {
    // TODO: Exception handling, NULL Verification, & bounded indexes
    arr[i] = arr[j].also { arr[j] = arr[i] }
}

// Sample of the code working
// Uncomment for testing
fun main(){
    val inputArr = arrayListOf('T', 'M', 'A', 'T', 'M', 'A', 'A', 'T')
    println("This is the original input arr: $inputArr")
    val outputArr = sortTamServer(inputArr)
    println("This is the new output array: $outputArr")
}