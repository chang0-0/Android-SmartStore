package com.ssafy.smartstore.fragment

import kotlinx.coroutines.*

fun main() = runBlocking{

//    coroutineScope {
//        delay(200L)
//        println("1")
//    }
//
//
//    coroutineScope {
//        coroutineScope {
//            delay(500L)
//            println("2")
//        }
//        delay(100L)
//        println("3")
//    }
//
//    println("4")

    test()
}

 fun test() {

    CoroutineScope(Dispatchers.Default).launch {
        coroutineScope {
            delay(200L)
            println("1")
        }

        coroutineScope {
            coroutineScope {
                delay(500L)
                println("2")
            }
            delay(100L)
            println("3")
        }

        println("4")
    }.job
}