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

### Run iOS Client

**Note**: The iOS client is the one experiencing memory issues and should be monitored closely.

1. **Start the server** (in a separate terminal):

   ```bash
   ./gradlew :server:runServer
   ```

2. **Open the Xcode project**:

   ```bash
   open iosApp/iosApp.xcodeproj
   ```

   Or manually open `iosApp/iosApp.xcodeproj` in Xcode.

3. **Build and run the iOS app**:

   - Select a simulator or connected device in Xcode
   - Click the Run button (▶️) or press `Cmd+R`
   - Xcode will automatically build the Kotlin Multiplatform framework and then build/launch the app

4. **Monitor memory usage**:

   - **Using Xcode's Debug Navigator**:
     - While debugging, open the Debug Navigator (left sidebar)
     - Monitor the Memory graph in real-time as the stream processes

5. **Interact with the app**:

   - Click "Connect to Server" button in the app
   - The app will connect to `http://localhost:8080` and start receiving the JSON stream
   - Watch for memory growth patterns and potential leaks

**Note**: For physical iOS devices, update the server URL in `App.kt` to use your computer's IP address instead of `localhost`.

## Configuration

You can modify the following in `server/src/main/kotlin/com/powersync/ktortest/Application.kt`:

- `totalMessages`: Number of messages to send (default: 1,000,000)
- `payloadSize`: Rough individual message size

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

- Uses Ktor OkHttp engine (the default)
- Run via Android Studio or `./gradlew :composeApp:installDebug`
- Server URL: `http://10.0.2.2:8080` (emulator) or your computer's IP (physical device)

### Desktop (JVM)

- Uses Ktor CIO engine (the default)
- Compose Multiplatform UI
- Run via: `./gradlew :composeApp:run`

### iOS

- Uses Ktor Darwin engine (NSURLSession)
- Compose Multiplatform UI
- Run via Xcode: Open `iosApp/iosApp.xcodeproj` and build/run (framework builds automatically)
- **Note**: This platform is experiencing memory issues when processing large JSON streams
- Server URL: `http://localhost:8080` (simulator) or your computer's IP (physical device)

## Notes

- The server sends NDJSON (newline-delimited JSON) format
- Each message is read but not stored to minimize memory impact
- Adjust message count and size based on your profiling needs
- The Compose Multiplatform UI works on Android, iOS, and Desktop (JVM)
