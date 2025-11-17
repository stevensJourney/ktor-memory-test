package com.powersync.ktortest

import io.ktor.http.ContentType
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

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
        
        get("/sync/stream") {
            // Use x-ndjson content type (same as PowerSync)
            call.respondTextWriter(contentType = ContentType("application", "x-ndjson")) {
                // Send a large number of JSON lines to simulate high data volume
                val totalMessages = 1_000_000 // 1 million messages
                val batchSize = 1000
                
                println("Server: Starting to send $totalMessages messages...")
                
                for (i in 0 until totalMessages) {
                    val message = SyncMessage(
                        type = "data",
                        data = mapOf(
                            "table" to "test_table",
                            "id" to i.toString(),
                            "value" to "This is a test value with some data ${"x".repeat(100)}",
                            "timestamp" to System.currentTimeMillis().toString()
                        ),
                        id = messageId++
                    )
                    
                    val jsonLine = json.encodeToString(message) + "\n"
                    write(jsonLine)
                    flush()
                    
                    // Small delay to simulate real network conditions
                    if (i % batchSize == 0) {
                        delay(1) // 1ms delay every 1000 messages
                        println("Server: Sent ${i + 1} messages")
                    }
                }
                
                println("Server: Finished sending all messages")
            }
        }
    }
}