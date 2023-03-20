package technology.iatlas.spaceup.common.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.ui.viewModel
import technology.iatlas.spaceup.common.components.Alert
import technology.iatlas.spaceup.common.model.Domain
import technology.iatlas.spaceup.common.util.httpClient
import technology.iatlas.spaceup.common.viewmodel.ServerViewModel

@Composable
fun Home(navigator: Navigator) {
    val coroutineScope = rememberCoroutineScope()
    var cached by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val domains = remember { mutableStateListOf<Domain>() }
    val openDialog = remember { mutableStateOf(false) }
    val errorMsg = remember { mutableStateOf("") }

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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    //text = "Hello, $platformName"
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            // "Authorization": 'Bearer $jwt'
                            val response = httpClient()
                                .get("${serverViewModel.serverUrl}/api/domain/list?cached=$cached") {
                                    headers {
                                        append("Authorization", "Bearer ${serverViewModel.token.accessToken}")
                                    }
                                }
                            if(response.status == HttpStatusCode.OK) {
                                response.body<List<Domain>>().forEach {
                                    if (!domains.contains(it)) {
                                        domains.add(it)
                                    }
                                }
                            } else {
                                // Show error
                                errorMsg.value = response.bodyAsText()
                                openDialog.value = true
                            }
                        } catch (ex: Exception) {
                            if(ex.message != null) {
                                errorMsg.value = ex.message!!
                            }
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
            Alert(openDialog, serverViewModel.serverUrl)
        }

    }
}

@OptIn(ExperimentalUnitApi::class)
@Composable
fun MessageList(domains: List<Domain>) {
    var expanded by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(domains.size) { index ->
            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp),
            ) {
                Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text(domains[index].url)
                    Spacer(modifier = Modifier.height(16.dp))
                    Column{
                        ClickableText(text = AnnotatedString("More Options"), onClick = {
                            expanded = true
                        })
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            //modifier = Modifier.widthIn(min = 120.dp)
                        ) {
                            DropdownMenuItem(onClick = { /* Aktion 1 */ }) {
                                Text("Aktion 1")
                            }
                            DropdownMenuItem(onClick = { /* Aktion 2 */ }) {
                                Text("Aktion 2")
                            }
                            Divider()
                            DropdownMenuItem(onClick = { /* Aktion 3 */ }) {
                                Text("Aktion 3")
                            }
                        }
                    }
                }
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
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}