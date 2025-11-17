package com.powersync.ktortest

import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.delay

class StreamClient(
    private val serverUrl: String = "http://10.0.2.2:8080", // Use 10.0.2.2 for Android emulator to access localhost
    private val httpClient: HttpClient = createDefaultHttpClient()
) {
    suspend fun connectAndStream() {
        println("Client: Connecting to $serverUrl/sync/stream...")
        
        try {
            val response: HttpResponse = httpClient.get("$serverUrl/sync/stream") {
                accept(ContentType("application", "x-ndjson"))
            }
            
            println("Client: Connected. Status: ${response.status}")
            println("Client: Starting to receive data...")
            
            var lineCount = 0
            val startTime = currentTimeMillis()
            
            // Read the response as a stream of lines using ByteReadChannel (same as PowerSync)
            val body: ByteReadChannel = response.body()
            
            while (!body.isClosedForRead) {
                val line = body.readUTF8Line()
                if (line != null && line.isNotBlank()) {
                    lineCount++
                    
                    // Print every 1000th line to avoid flooding console
                    if (lineCount % 1000 == 0) {
                        val elapsed = (currentTimeMillis() - startTime) / 1000
                        println("Client: Received $lineCount lines (${elapsed}s elapsed)")
                    }
//                    delay(1000)
                } else if (line == null) {
                    // End of stream
                    break
                }
            }
            
            val elapsed = (currentTimeMillis() - startTime) / 1000.0
            println("Client: Finished. Received $lineCount lines in ${elapsed}s")
            if (elapsed > 0) {
                println("Client: Average rate: ${(lineCount / elapsed).toInt()} lines/second")
            }
            
        } catch (e: Exception) {
            println("Client: Error - ${e.message}")
            e.printStackTrace()
        } finally {
            httpClient.close()
        }
    }
}

