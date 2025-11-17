package com.powersync.ktortest

import kotlinx.datetime.Clock

actual fun currentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

