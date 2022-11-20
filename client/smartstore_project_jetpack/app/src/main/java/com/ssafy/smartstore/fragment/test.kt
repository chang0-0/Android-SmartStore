import kotlinx.coroutines.*

fun main() {
    works()
    readLine()
}

private fun works(){
    val one = GlobalScope.async {
        goSchool()
    }
    val two = GlobalScope.async {
        goHome()
    }

    GlobalScope.launch{
        val combined = one.await() + " " + two.await()
        println("Combined : $combined")
    }


}

suspend fun goSchool(): String{
    delay(3000)
    return "arrive school"
}

suspend fun goHome(): String{
    delay(1000)
    return "arrive Home"
}