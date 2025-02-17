// File: main.kt
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.exitProcess
import kotlinx.coroutines.*
import androidx.compose.ui.graphics.Color
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
                is Screen.Landing -> landingPage(
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
                is Screen.Overview -> overviewPage(
                    onBackClick = { currentScreen = Screen.Landing }
                )
                is Screen.Client -> clientPage(
                    onBackClick = { currentScreen = Screen.Landing }
                )
            }
        }
    }
}

@Composable
fun landingPage(onOverviewClick: () -> Unit,
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
        Text(text = "CSCE 3335 Project 1: Client-Server TAM Sorter",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center)
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
        Button(
            onClick = { exitProcess(0) },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Exit")
        }

    }
}

@Composable
fun overviewPage(onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Project Overview", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This project implements a client-server application using TCP/IP socket programming " +
                    "integrated with a Compose Multiplatform UI and basic navigation between screens. " +
                    "The client collects a user-defined input string composed of the letters T, A, and M (terminated by a '#'), " +
                    "stores it in an array (TAM_TAB_Client), and sends it to the server. The server receives this input into " +
                    "an array (TAM_TAB_Server) and sorts it using a specialized algorithm (Sort_TAM_Server) that reorders " +
                    "the letters so that all T's appear first, followed by A's, and then M's. This sorting algorithm " +
                    "operates in-place, evaluates each character only once, and only performs swaps when necessary, " +
                    "meeting strict design constraints. Once sorted, the server returns both the original and sorted arrays " +
                    "to the client for display. \nCreated by: Alejandro Aguilar & Gael Mota",
            style = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}

@Composable
fun clientPage(onBackClick: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    var inputText by remember { mutableStateOf("") }
    val outputMessages = remember { mutableStateListOf<String>() }


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Top bar with Back button.
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Button(onClick = onBackClick) {
                Text("Back")
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Clear text button
            Button(onClick = {outputMessages.clear()}) {
                Text("Clear Text")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Instruction text.
        Text(
            text = "Enter a sequence of capital T's, M's, and A's. End it with a '#' sign.",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Output area with vertical scroll.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
                .border(width = 1.dp, color = Color.Black)
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
                val input = inputText.trim()
                if (!input.endsWith("#")) {
                    outputMessages.add("Error: Message must end with '#'")
                } else {
                    val messageContent = input.substringBefore("#")
                    if (messageContent.isNotEmpty()) {
                        outputMessages.add("Sending: $messageContent")
                        inputText = ""
                        // Use the function from client.kt for sending a message.
                        coroutineScope.launch {
                            val responses = sendMessageExportable(messageContent)
                            responses.forEach { response ->
                                outputMessages.add(response)
                            }
                        }
                    } else {
                        outputMessages.add("Error: Message cannot be empty")
                    }
                }
            },

            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send")
        }
    }
}