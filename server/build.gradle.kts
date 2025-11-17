plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
    application
}

group = "com.powersync.ktortest"
version = "1.0.0"
application {
    mainClass.set("com.powersync.ktortest.MainKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.kotlinx.serializationJson)
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}

// Custom task to run the server
tasks.register<JavaExec>("runServer") {
    group = "application"
    description = "Run the server"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.powersync.ktortest.MainKt")
    dependsOn("classes")
}