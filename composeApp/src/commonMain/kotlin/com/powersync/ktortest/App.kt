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
        
        // Helper function to get server URL
        val getServerUrl: () -> String = remember {
            {
                when {
                    PlatformDetector.isAndroid() -> "http://10.0.2.2:${SERVER_PORT}"
                    PlatformDetector.isIOS() -> "http://localhost:${SERVER_PORT}"
                    else -> "http://localhost:${SERVER_PORT}"
                }
            }
        }
        
        // Helper function to connect with a client
        val connectWithClient: (StreamClientInterface) -> Unit = remember(coroutineScope) {
            { client ->
                if (!isConnecting) {
                    isConnecting = true
                    connectionStatus = "Connecting..."
                    errorMessage = null
                    
                    coroutineScope.launch {
                        try {
                            connectionStatus = "Connected. Receiving data..."
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
            }
        }
        
        val serverUrl = remember { getServerUrl() }
        
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
                text = "Stream Client",
                style = MaterialTheme.typography.headlineMedium
            )
            
            // Ktor Client Button
            Button(
                onClick = {
                    val client = StreamClient(serverUrl = serverUrl)
                    connectWithClient(client)
                },
                enabled = !isConnecting,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                if (isConnecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isConnecting) "Connecting..." else "Connect with Ktor")
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