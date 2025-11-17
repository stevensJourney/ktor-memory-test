package com.powersync.ktortest

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

expect fun createHttpClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient

fun createDefaultHttpClient(): HttpClient {
    return createHttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
}

