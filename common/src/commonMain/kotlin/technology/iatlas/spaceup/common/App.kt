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
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
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
import io.ktor.client.request.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import technology.iatlas.spaceup.common.util.httpClient

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    var cached by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val domains = remember { mutableStateListOf<Domain>() }

    //var text by remember { mutableStateOf("Hello, World!") }
    //val platformName = getPlatformName()
    //var text by remember { mutableStateOf("Get Domains") }

    Scaffold(
        topBar = { MyToolbar() }
    ) {
        Column {
            Row {
                Button(
                    onClick = {
                        //text = "Hello, $platformName"
                        scope.launch {
                            isLoading = true
                            httpClient().get<List<Domain>>("http://192.168.178.17:9090/api/domain/list?cached=$cached").onEach {
                                if(!domains.contains(it)) {
                                    domains.add(it)
                                }
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
            if(!isLoading) {
                MessageList(domains)
            } else {
                if(!cached) Loader(isLoading)
            }

        }
    }
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