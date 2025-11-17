package com.powersync.ktortest

import io.ktor.client.*
import io.ktor.client.engine.cio.*

actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(CIO, block)

