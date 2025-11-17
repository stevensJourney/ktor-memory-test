package com.powersync.ktortest

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

object PlatformDetector {
    fun isAndroid(): Boolean = getPlatform().name.contains("Android", ignoreCase = true)
    fun isIOS(): Boolean {
        val name = getPlatform().name
        return name.contains("iOS", ignoreCase = true) || 
               name.contains("iPhone", ignoreCase = true) ||
               name.contains("iPad", ignoreCase = true)
    }
    fun isJVM(): Boolean = getPlatform().name.contains("Java", ignoreCase = true)
}