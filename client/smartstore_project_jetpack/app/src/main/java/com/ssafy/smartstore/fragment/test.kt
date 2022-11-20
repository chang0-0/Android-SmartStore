package com.ssafy.smartstore.fragment

import kotlinx.coroutines.*

fun main() {

    CoroutineScope(Dispatchers.Default).launch {
        delay(200L)
        println("1")
    }

    CoroutineScope(Dispatchers.Default).launch {
        coroutineScope {
            delay(500L)
            println("2")
        }
        delay(100L)
        println("3")
    }

    println("4")
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