package technology.iatlas.spaceup.common.views

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.ui.viewModel
import technology.iatlas.spaceup.common.components.Alert
import technology.iatlas.spaceup.common.components.FullscreenCircularLoader
import technology.iatlas.spaceup.common.model.Domain
import technology.iatlas.spaceup.common.openInBrowser
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            println("Get domains with ${serverViewModel.token}")
                            val response = serverViewModel.client()
                                .get("${serverViewModel.serverUrl}/api/domain/list?cached=$cached")
                            if (response.status == HttpStatusCode.OK) {
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
                            if (ex.message != null) {
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
        }

        // Domain content
        MessageList(domains)

        if (openDialog.value) {
            Alert(openDialog, serverViewModel.serverUrl)
        }

        if (!cached) FullscreenCircularLoader(isLoading)
    }

    LaunchedEffect(Unit) {
        try {
            // TODO move this to domainViewModel
            // "Authorization": 'Bearer $jwt'
            val response = serverViewModel.client()
                .get("${serverViewModel.serverUrl}/api/domain/list?cached=true") {
                    headers {
                        append("Authorization", "Bearer ${serverViewModel.token.accessToken}")
                    }
                }
            if (response.status == HttpStatusCode.OK) {
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
            if (ex.message != null) {
                errorMsg.value = ex.message!!
            }
            openDialog.value = true
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MessageList(domains: List<Domain>) {
    LazyColumn(
        modifier = Modifier.padding(vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(domains.size) { index ->
            Card(
                modifier = Modifier
                    .height(60.dp)
                    .padding(4.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                val expanded = mutableStateOf(false)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = domains[index].url,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        IconButton(
                            onClick = {
                                expanded.value = !expanded.value
                            }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    "More"
                                )
                                DropdownMenu(
                                    expanded = expanded.value,
                                    onDismissRequest = { expanded.value = false },
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Open") },
                                        onClick = {
                                            openInBrowser("https://${domains[index].url}")
                                        })
                                    Divider()
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Delete",
                                                style = TextStyle(color = MaterialTheme.colorScheme.error)
                                            )
                                        },
                                        onClick = {
                                            // Delete Domain with Alert warning
                                        })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
