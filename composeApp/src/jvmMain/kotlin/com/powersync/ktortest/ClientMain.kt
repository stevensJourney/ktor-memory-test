package com.powersync.ktortest

import kotlinx.coroutines.*

fun main() {
    println("Starting Ktor Stream Client...")
    println("This will connect to http://localhost:$SERVER_PORT/sync/stream")
    println("Monitor memory usage while this runs")
    
    runBlocking {
        StreamClient(serverUrl = "http://localhost:$SERVER_PORT").connectAndStream()
    }
}

