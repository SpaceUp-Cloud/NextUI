package technology.iatlas.spaceup.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.rememberNotification
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import technology.iatlas.spaceup.common.util.httpClient

@Composable
fun App() {
    val coroutineScope = rememberCoroutineScope()
    var cached by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val domains = remember { mutableStateListOf<Domain>() }
    val baseHost = remember { mutableStateOf("https://") }

    //var text by remember { mutableStateOf("Hello, World!") }
    //val platformName = getPlatformName()
    //var text by remember { mutableStateOf("Get Domains") }
    val openDialog = remember { mutableStateOf(false) }
    val notification = rememberNotification("Found Domains", "xxx domains", Notification.Type.Info)

    Scaffold(
        topBar = { MyToolbar() }
    ) {
        Column {
            Row {
                TextField(
                    value = baseHost.value,
                    onValueChange = { baseHost.value = it },
                    label = {
                        Text("SpaceUp-Server: ${baseHost.value}")
                    },
                    singleLine = true,
                    placeholder = {
                        Text("http://your.spaceup.server")
                    }
                )
            }
            Row {
                Button(
                    onClick = {
                        //text = "Hello, $platformName"
                        coroutineScope.launch {
                            isLoading = true
                            try {
                                val response = httpClient().get("${baseHost.value}/api/domain/list?cached=$cached")
                                if(response.status == HttpStatusCode.OK) {
                                    response.body<List<Domain>>().forEach {
                                        if (!domains.contains(it)) {
                                            domains.add(it)
                                        }
                                    }
                                }
                            } catch (ex: Exception) {
                                openDialog.value = true
                            }
                            isLoading = false
                        }
                    }) {
                    Text("Get Domains")
                }
                Button(
                    onClick = {
                        domains.clear()
                    }
                ) {
                    Text("Clear Domains")
                }
                Text("Cached")
                Checkbox(
                    checked = cached,
                    onCheckedChange = { isChecked ->
                        cached = isChecked
                    }
                )
            }
            if (!isLoading) {
                MessageList(domains)
            } else {
                if (!cached) Loader(isLoading)
            }

            if (openDialog.value) {
                Alert(openDialog, baseHost)
            }

        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Alert(openDialog: MutableState<Boolean>, baseHost: MutableState<String>) {
    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
        },
        confirmButton = {
            Button(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text("Close")
            }
        },
        title = {
            Text("Error")
        },
        text = {
            val baseMsg = "Cannot connect to host: %s"
            val msg = if(baseHost.value.isEmpty()) baseMsg.replace(": %s", "")
                else baseMsg.replace("%s", baseHost.value)
            Text(msg)
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun MyToolbar() {
    TopAppBar(
        title = { Text("My App") },
        navigationIcon = {
            IconButton(onClick = { /* handle navigation icon click */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = { /* handle action click */ }) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )
}

@Composable
fun MessageList(domains: List<Domain>) {
    LazyColumn {
        items(domains.size) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    //.align(Alignment.Center)
                    .height(50.dp)
                    .padding(8.dp),
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(domains[index].url)
            }
        }
    }
}

@Composable
fun LoginScreen() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Row {
        Column {
            TextField(value = username, onValueChange = { username = it })
            TextField(value = password, onValueChange = { password = it })
        }
    }
}

@Composable
fun Loader(isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

@Serializable
data class Domain(val url: String)