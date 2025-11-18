package com.powersync.ktortest

import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SyncMessage(
    val type: String,
    val data: Map<String, String> = emptyMap(),
    val id: Int
)

fun Application.module() {
    val json = Json { ignoreUnknownKeys = true }
    var messageId = 0
    
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }
        
        post("/sync/stream") {
            // Set headers to ensure proper streaming behavior
            // Note: Transfer-Encoding: chunked is automatically set by Ktor when using respondTextWriter
            call.response.header("Cache-Control", "no-cache")
            call.response.header("Connection", "keep-alive")
            call.response.header("X-Accel-Buffering", "no") // Disable buffering in nginx if behind proxy
            
            // Use x-ndjson content type (same as PowerSync)
            // respondTextWriter automatically enables chunked transfer encoding
            call.respondTextWriter(contentType = ContentType("application", "x-ndjson")) {
                // Send a large number of JSON lines to simulate high data volume
                val totalMessages = 1_000_000 // 1 million messages
                val batchSize = 1000
                
                println("Server: Starting to send $totalMessages messages...")
                // Create a 1MB payload string (accounting for JSON encoding overhead)
                // 1MB = 1,048,576 bytes. JSON encoding adds ~200-300 bytes for structure,
                // so we'll use ~1,048,000 bytes for the actual payload data
                val payloadSize = 1_048_000
                val largePayload = "x".repeat(payloadSize)
                
                for (i in 0 until totalMessages) {
                    val message = SyncMessage(
                        type = "data",
                        data = mapOf(
                            "table" to "test_table",
                            "id" to i.toString(),
                            "value" to "This is a test value with some data",
                            "payload" to largePayload, // 1MB payload
                            "timestamp" to System.currentTimeMillis().toString()
                        ),
                        id = messageId++
                    )
                    
                    val jsonLine = json.encodeToString(message) + "\n"
                    write(jsonLine)
                    flush()
                    
                    // Small delay to simulate real network conditions
                    if (i % batchSize == 0) {
                        println("Server: Sent ${i + 1} messages")
                    }
                }
                
                println("Server: Finished sending all messages")
            }
        }
    }
}