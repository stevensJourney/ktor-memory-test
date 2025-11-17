package com.powersync.ktortest

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    println("Starting Ktor Stream Server on port $SERVER_PORT...")
    println("This will send 1 million JSON messages to simulate high data volume")
    println("Press Ctrl+C to stop")
    // Use the Application.module() from Application.kt
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}
