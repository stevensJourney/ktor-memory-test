package com.powersync.ktortest

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*

actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient = HttpClient(OkHttp, block)

