package com.powersync.ktortest

import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line

class StreamClient(
    private val serverUrl: String = "http://10.0.2.2:8080", // Use 10.0.2.2 for Android emulator to access localhost
    private val httpClient: HttpClient = createDefaultHttpClient()
) : StreamClientInterface {
    override suspend fun connectAndStream() {
        println("Client: Connecting to $serverUrl/sync/stream...")
        
        try {
            val ndjson = ContentType("application", "x-ndjson")
            val uri = "$serverUrl/sync/stream"
            
            val request = httpClient.preparePost(uri) {
                contentType(ContentType.Application.Json)
                headers {
                    accept(ndjson)
                }
                setBody("") // Empty POST body
            }
            
            request.execute { httpResponse ->
                println("Client: Connected. Status: ${httpResponse.status}")
                
                if (httpResponse.status != HttpStatusCode.OK) {
                    throw RuntimeException("Received error when connecting to sync stream: ${httpResponse.bodyAsText()}")
                }
                
                println("Client: Starting to receive data...")
                
                var lineCount = 0
                
                // Read the response as a stream of lines using ByteReadChannel (same as PowerSync)
                val body: ByteReadChannel = httpResponse.body<ByteReadChannel>()
                
                println("Client: Starting to read stream...")
                
                while (!body.isClosedForRead) {
                    val line = body.readUTF8Line()
                    if (line != null && line.isNotBlank()) {
                        lineCount++

                        println("line count is $lineCount")
                        
                        // Print every 1000th line to avoid flooding console
                        if (lineCount % 1000 == 0) {
                            println("Client: Received $lineCount lines")
                        }
                    } else if (line == null) {
                        // End of stream
                        println("Client: Stream ended (line is null)")
                        break
                    }
                }
                
                println("Client: Finished. Received $lineCount lines total")
            }
            
        } catch (e: Exception) {
            println("Client: Error - ${e.message}")
            e.printStackTrace()
        } finally {
            httpClient.close()
        }
    }
}

