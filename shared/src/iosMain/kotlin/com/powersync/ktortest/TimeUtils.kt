package com.powersync.ktortest

import kotlin.time.Clock

actual fun currentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

