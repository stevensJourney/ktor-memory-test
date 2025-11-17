# Ktor Stream Profiling Project

A minimal reproducible project to profile Ktor client memory usage when receiving JSON streams.

## Purpose

This project helps reproduce and profile high memory usage in Ktor clients when receiving large JSON streams, similar to PowerSync's `sync/stream` endpoint.

## Project Structure

This is a Kotlin Multiplatform project with:

- **JVM target**: Server and command-line client
- **Android target**: Android app client using OkHttp engine
- **iOS target**: iOS app client (Compose Multiplatform)

- **Server** (`server/src/main/kotlin/Application.kt`): A Ktor server that streams 1 million JSON messages
- **Server Main** (`server/src/main/kotlin/Main.kt`): JVM entry point to run the server
- **Client** (`shared/src/commonMain/kotlin/Client.kt`): A Ktor client that receives and processes the stream (shared code)
- **Client Main** (`composeApp/src/jvmMain/kotlin/ClientMain.kt`): JVM entry point to run the command-line client
- **App** (`composeApp/src/commonMain/kotlin/App.kt`): Compose Multiplatform UI with "Connect to Server" button

## Running the Project

### Build

```bash
./gradlew build
```

### Run Server

In one terminal:

```bash
./gradlew :server:runServer
```

Or:

```bash
./gradlew :server:run
```

The server will start on `http://localhost:8080` and begin streaming JSON messages.

### Run Client (JVM)

In another terminal:

```bash
./gradlew :composeApp:runClient
```

The client will connect to the server and start receiving/printing JSON lines.

### Run Android Client

1. Start the server: `./gradlew :server:runServer`
2. Open the project in Android Studio
3. Run the Android app on an emulator or device
4. Click "Connect to Server" button in the app
5. Use Android Studio's built-in profiler to monitor memory usage

**Note**: The Android client uses `http://10.0.2.2:8080` to connect to localhost on the emulator. For physical devices, update the server URL in `App.kt` to use your computer's IP address.

### Run Desktop Client (JVM)

1. Start the server: `./gradlew :server:runServer`
2. Run the desktop app:
   ```bash
   ./gradlew :composeApp:run
   ```
3. Click "Connect to Server" button in the app

## Profiling

### Using Android Studio Profiler (Recommended for Android)

1. Start the server: `./gradlew :server:runServer`
2. Run the Android app from Android Studio
3. Open **View → Tool Windows → Profiler** (or click the Profiler tab)
4. Select your running app process
5. Click **Memory** to monitor heap usage, allocations, and GC events
6. Click "Connect to Server" in the app
7. Watch memory usage in real-time

The Android Studio profiler provides:

- Real-time heap usage graphs
- Object allocation tracking
- GC event visualization
- Memory leak detection
- Method-level CPU profiling

### Using JProfiler / VisualVM / YourKit

1. Start the server: `./gradlew :server:runServer`
2. Attach your profiler to the client process
3. Start the client: `./gradlew :composeApp:runClient`
4. Monitor memory usage, heap dumps, and object allocations

### Using JVM Memory Options

Run the client with memory profiling flags:

```bash
./gradlew :composeApp:runClient \
  -Dorg.gradle.jvmargs="-Xmx32m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof"
```

### Using Async Profiler

```bash
# Attach to the running client process
java -jar async-profiler.jar -e alloc -d 60 -f profile.html <pid>
```

## Configuration

You can modify the following in `server/src/main/kotlin/com/powersync/ktortest/Application.kt`:

- `totalMessages`: Number of messages to send (default: 1,000,000)
- `batchSize`: Delay interval (default: 1000)
- Message size: Modify the data in `SyncMessage` to increase payload size

## Expected Behavior

- Server sends JSON lines continuously
- Client receives and parses each line
- Memory usage should be monitored to identify any leaks or excessive buffering
- The client prints progress every 1000 lines

## Platform-Specific Details

### JVM (Server & CLI Client)

- Server uses Netty
- Client uses Ktor CIO engine
- Run server via: `./gradlew :server:runServer`
- Run client via: `./gradlew :composeApp:runClient`

### Android

- Uses Ktor OkHttp engine (Android-optimized)
- Requires Android SDK 24+
- Run via Android Studio or `./gradlew :composeApp:installDebug`
- Server URL: `http://10.0.2.2:8080` (emulator) or your computer's IP (physical device)

### Desktop (JVM)

- Uses Ktor CIO engine
- Compose Multiplatform UI
- Run via: `./gradlew :composeApp:run`

## Notes

- The server sends NDJSON (newline-delimited JSON) format
- Each message is parsed but not stored to minimize memory impact
- Adjust message count and size based on your profiling needs
- Android client uses OkHttp engine which may have different memory characteristics than CIO
- The Compose Multiplatform UI works on Android, iOS, and Desktop (JVM)
