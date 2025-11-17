package com.powersync.ktortest

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import ktortest.composeapp.generated.resources.Res
import ktortest.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        var connectionStatus by remember { mutableStateOf<String?>(null) }
        var isConnecting by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val coroutineScope = rememberCoroutineScope()
        
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Ktor Stream Client",
                style = MaterialTheme.typography.headlineMedium
            )
            
            Button(
                onClick = {
                    if (!isConnecting) {
                        isConnecting = true
                        connectionStatus = "Connecting..."
                        errorMessage = null
                        
                        coroutineScope.launch {
                            try {
                                // Determine server URL based on platform
                                val serverUrl = when {
                                    // Android emulator
                                    PlatformDetector.isAndroid() -> "http://10.0.2.2:${SERVER_PORT}"
                                    // iOS simulator or physical device
                                    PlatformDetector.isIOS() -> "http://localhost:${SERVER_PORT}"
                                    // JVM/Desktop
                                    else -> "http://localhost:${SERVER_PORT}"
                                }
                                
                                connectionStatus = "Connected. Receiving data..."
                                
                                val client = StreamClient(serverUrl = serverUrl)
                                client.connectAndStream()
                                
                                connectionStatus = "Connection completed successfully!"
                            } catch (e: Exception) {
                                errorMessage = "Error: ${e.message}"
                                connectionStatus = "Connection failed"
                            } finally {
                                isConnecting = false
                            }
                        }
                    }
                },
                enabled = !isConnecting
            ) {
                if (isConnecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isConnecting) "Connecting..." else "Connect to Server")
            }
            
            AnimatedVisibility(connectionStatus != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (connectionStatus != null) {
                        Text(
                            text = connectionStatus!!,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            AnimatedVisibility(connectionStatus != null && errorMessage == null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: ${Greeting().greet()}")
                }
            }
        }
    }
}