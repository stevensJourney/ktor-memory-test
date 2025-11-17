package com.powersync.ktortest

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
    TODO("Not yet implemented")
}