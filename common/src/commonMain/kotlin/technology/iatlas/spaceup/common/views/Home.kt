package technology.iatlas.spaceup.common.views

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.ui.viewModel
import technology.iatlas.spaceup.common.components.Alert
import technology.iatlas.spaceup.common.model.Domain
import technology.iatlas.spaceup.common.util.httpClient
import technology.iatlas.spaceup.common.viewmodel.ServerViewModel

@Composable
@Preview
fun Home(navigator: Navigator) {
    val coroutineScope = rememberCoroutineScope()
    var cached by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val domains = remember { mutableStateListOf<Domain>() }
    val baseHost = remember { mutableStateOf("https://") }

    val openDialog = remember { mutableStateOf(false) }

    val serverViewModel = viewModel(ServerViewModel::class) {
        ServerViewModel()
    }

    Column {
        Row {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = serverViewModel.serverUrl,
                onValueChange = { serverViewModel.serverUrl = it },
                label = {
                    Text("SpaceUp-Server: ${serverViewModel.serverUrl}")
                },
                singleLine = true,
                placeholder = {
                    Text(serverViewModel.serverUrl)
                }
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
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
                            } else {
                                // Show error
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
            Button(
                onClick = {
                    navigator.navigate("/login")
                }
            ) {
                Text("Logout")
            }
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