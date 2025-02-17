// File: main.kt
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess
import kotlinx.coroutines.*
//import client.sendMessageExportable // Importing from client.kt file

// Sealed class for navigation screens
sealed class Screen {
    object Landing : Screen()
    object Overview : Screen()
    object Client : Screen()
}

fun main() = application {
    val appScope = remember { CoroutineScope(Dispatchers.IO) }
    Window(onCloseRequest = {
        appScope.cancel()
        exitProcess(0) }, title = "Client GUI") {
        MaterialTheme {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Landing) }
            var isServerRunning by remember { mutableStateOf(false) }
            var serverJob by remember { mutableStateOf<Job?>(null) }
            var canToggleServer by remember { mutableStateOf(true) }

            when (currentScreen) {
                is Screen.Landing -> LandingPage(
                    onOverviewClick = { currentScreen = Screen.Overview },
                    onClientClick = { currentScreen = Screen.Client },
                    isServerRunning = isServerRunning,
                    serverJob = serverJob,
                    onToggleServer = { newState, newJob ->
                        isServerRunning = newState
                        serverJob = newJob
                    },
                    onUpdateCanToggle = { newValue -> canToggleServer = newValue },
                    canToggleServer = canToggleServer,
                    appScope = appScope
                )
                is Screen.Overview -> OverviewPage(
                    onBackClick = { currentScreen = Screen.Landing }
                )
                is Screen.Client -> ClientPage(
                    onBackClick = { currentScreen = Screen.Landing }
                )
            }
        }
    }
}

@Composable
fun LandingPage(onOverviewClick: () -> Unit,
                onClientClick: () -> Unit,
                appScope: CoroutineScope,
                isServerRunning: Boolean,
                serverJob: Job?,
                canToggleServer: Boolean,
                onToggleServer: (Boolean, Job?) -> Unit,
                onUpdateCanToggle: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Welcome!", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onOverviewClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Overview of Project")
        }
        Button(
            onClick = onClientClick,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Run Client")
        }
        Button(
            onClick = {
                if (!isServerRunning) {
                    // Start Server logic
                    val job = appScope.launch {
                        startServer(9999)
                    }
                    onToggleServer(true, job)
                } else {
                    // stop server logic
                    currentServerSocket?.close()

                    serverJob?.cancel() // Triggers CancellationException in startServer
                    onToggleServer(false, null)

                    onUpdateCanToggle(false)
                    appScope.launch {
                        delay(1500)
                        onUpdateCanToggle(true)
                        // Display some message or something idk
                    }
                }
            },
            enabled = canToggleServer,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(if (isServerRunning) "Stop Server" else "Start Server")
        }

    }
}

@Composable
fun OverviewPage(onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Project Overview", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This project demonstrates a simple socket client integrated with " +
                    "a Compose Multiplatform UI and basic navigation between pages.",
            style = MaterialTheme.typography.body1
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}

@Composable
fun ClientPage(onBackClick: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    val outputMessages = remember { mutableStateListOf<String>() }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Top bar with Back button.
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Button(onClick = onBackClick) {
                Text("Back")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Output area with vertical scroll.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            Column {
                outputMessages.forEach { message ->
                    Text(text = message)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Input field.
        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter message") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Send button.
        Button(
            onClick = {
                val trimmedInput = inputText.trim().let {
                    if (it.endsWith("#")) it.dropLast(1) else it
                }
                if (trimmedInput.isNotEmpty()) {
                    outputMessages.add("Sending: $trimmedInput")
                    inputText = ""
                    // Use the function from client.kt for sending a message.
                    coroutineScope.launch {
                        val responses = sendMessageExportable(trimmedInput)
                        responses.forEach { response ->
                            outputMessages.add(response)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send")
        }
    }
}