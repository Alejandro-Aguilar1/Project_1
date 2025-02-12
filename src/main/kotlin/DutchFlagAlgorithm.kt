fun dutchFlag(arr: ArrayList<Char>): ArrayList<Char> {
    /*
    Given some array with 0s, 1s, and 2s, sort the array so that
    the left contain all the 0s, the right contains all the 2s; and
    the 1s are in between these 0s and 2s.
     */
    var low: Int = 0
    var high: Int = arr.size - 1
    var mid: Int = 0
    val outputArr = ArrayList(arr)

    while (mid <= high) {
        // If statement implementation
//        if (outputArr[mid] == 'T') {
//            outputArr[low] = outputArr[mid].also { outputArr[mid] = outputArr[low] }
//            mid ++
//            low ++
//        } else if (outputArr[mid] == 'A') {
//            mid ++
//        } else {
//            outputArr[mid] = outputArr[high].also { outputArr[high] = outputArr[mid] }
//            high -= 1
//        }

        // 'when' implementation
        when (outputArr[mid]) {
            'T' -> {
                outputArr[low] = outputArr[mid].also { outputArr[mid] = outputArr[low] }
                mid ++
                low ++
            }
            'A' -> mid ++
            else -> {
                outputArr[mid] = outputArr[high].also { outputArr[high] = outputArr[mid] }
                high -= 1
            }
        }
    }
    return outputArr
}

// Sample of the code working
// Uncomment for testing
//fun main(){
//    val inputArr = arrayListOf('T', 'M', 'A', 'T', 'M', 'A', 'A', 'T')
//    val outputArr = dutchFlag(inputArr)
//    println("This is the original input arr: $inputArr")
//    println("This is the new output array: $outputArr")
//}