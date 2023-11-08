package technology.iatlas.spaceup.common.pages

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.stateholder.LocalSavedStateHolder
import technology.iatlas.spaceup.common.OpenInBrowser
import technology.iatlas.spaceup.common.components.ErrorAlert
import technology.iatlas.spaceup.common.components.FullscreenCircularLoader
import technology.iatlas.spaceup.common.model.Domain
import technology.iatlas.spaceup.common.model.SettingsConstants
import technology.iatlas.spaceup.common.util.httpClient
import technology.iatlas.spaceup.common.viewmodel.ServerViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DomainsView() {
    val logger = KotlinLogging.logger {}

    val settings = Settings()

    val cached = remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(false) }
    val domains = remember { mutableStateListOf<Domain>() }
    val openDialog = remember { mutableStateOf(false) }
    val errorMsg = remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(true) }

    val stateHolder = LocalSavedStateHolder.current
    val serverViewModel = koinViewModel(ServerViewModel::class) { org.koin.core.parameter.parametersOf(stateHolder) }
    val serverUrl = serverViewModel.serverUrl

    val client =  httpClient(settings.getString(SettingsConstants.ACCESS_TOKEN.toString(), ""))

    // Create grid view layout



    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        FlowRow {
            Button(
                enabled = isEnabled,
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        isLoading = true
                        isEnabled = false
                        try {
                            val response = client
                                .get("${serverUrl}/api/domain/list?cached=${cached.value}")
                            if (response.status == HttpStatusCode.OK) {
                                val domainList = response.body<List<Domain>>()
                                logger.info { "Received domains: $domainList" }
                                domainList.forEach {
                                    if (!domains.contains(it)) {
                                        domains.add(it)
                                    }
                                }
                            } else {
                                // Show error
                                errorMsg.value = response.status.description
                                openDialog.value = true
                            }
                        } catch (ex: Exception) {
                            if (ex.message != null) {
                                errorMsg.value = ex.message!!
                            }
                            openDialog.value = true
                        }
                        isLoading = false
                        isEnabled = true
                    }
                }) {
                Text("Get Domains")
            }
            Button(
                enabled = isEnabled,
                onClick = {
                    domains.clear()
                }
            ) {
                Text("Clear Domains")
            }
            if(isEnabled) {
                CheckBoxTextGroup("cached", cached)
            }
        }

        if (openDialog.value) {
            ErrorAlert(openDialog, errorMsg.value)
        }

        if (!cached.value && isLoading) {
            FullscreenCircularLoader(isLoading)
        } else {
            // Domain content
            MessageList(domains)
        }
    }

    LaunchedEffect(Unit) {
        try {
            val response = client
                .get("${serverUrl}/api/domain/list?cached=${cached.value}")
            if (response.status == HttpStatusCode.OK) {
                response.body<List<Domain>>().forEach {
                    if (!domains.contains(it)) {
                        domains.add(it)
                    }
                }
            } else {
                // Show error
                errorMsg.value = response.status.description
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
    var domain by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.padding(vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(domains.size) { index ->
            Card(
                modifier = Modifier
                    .height(35.dp)
                    .padding(4.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                val expanded = mutableStateOf(false)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = domains[index].url,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
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
                                        domain = domains[index].url
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

    if(domain.isNotEmpty()) {
        OpenInBrowser("https://${domain}").also {
            domain = ""
        }
    }
}

@Composable
fun CheckBoxTextGroup(
    text: String,
    checked: MutableState<Boolean>
) {
    Row {
        Text(
            modifier = Modifier.padding(vertical = 10.dp),
            text = text
        )
        Checkbox(
            checked = checked.value,
            onCheckedChange = { isChecked ->
                checked.value = isChecked
            },
            modifier = Modifier.absoluteOffset((-12).dp, 0.dp)
        )
    }
}