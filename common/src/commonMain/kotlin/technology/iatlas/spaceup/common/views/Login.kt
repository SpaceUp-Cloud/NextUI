package technology.iatlas.spaceup.common.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.ui.viewModel
import technology.iatlas.spaceup.common.components.Alert
import technology.iatlas.spaceup.common.model.Routes
import technology.iatlas.spaceup.common.model.SettingsConstants
import technology.iatlas.spaceup.common.viewmodel.AuthenticationViewModel
import technology.iatlas.spaceup.common.viewmodel.ServerViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Login(navigator: Navigator) {
    /*
    Center screened components
    - SpaceUp Server
    - Username
    - Password
     */
    val logger = KotlinLogging.logger { }
    var isLoading by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    val passwordVisible = remember { mutableStateOf(false) }
    val errorMsg = remember { mutableStateOf("") }

    val focusRequesterUrl = remember { FocusRequester() }
    val focusRequesterUsername = remember { FocusRequester() }
    val focusRequesterPassword = remember { FocusRequester() }
    val focusRequesterSettings = remember { FocusRequester() }

    val settings = Settings()

    var rememberServer by remember { mutableStateOf(
        settings.getBoolean(SettingsConstants.REMEMBER_SERVER.toString(), false)) }
    var rememberUserPassword by remember { mutableStateOf(
        settings.getBoolean(SettingsConstants.REMEMBER_CREDENTIALS.toString(), false)) }

    val coroutineScope = rememberCoroutineScope()
    val serverViewModel = viewModel(ServerViewModel::class) {
        ServerViewModel()
    }
    val authenticationViewModel = viewModel(AuthenticationViewModel::class) {
        AuthenticationViewModel()
    }

    Column(
        // modifier = Modifier.fillMaxSize(), // To make it center center
        modifier = Modifier
            .padding(top = 48.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedCard(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(48.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 36.dp)
                    .align(Alignment.CenterHorizontally),
                text = "SpaceUp Login",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(
            modifier = Modifier
                .focusRequester(focusRequesterUrl)
                .onKeyEvent { keyEvent ->
                    handleKeyEvents(navigator, keyEvent,
                        onTab = {
                            focusRequesterUsername.requestFocus()
                        }
                    )
                },
            label = {
                Text("SpaceUp-Server")
            },
            value = serverViewModel.serverUrl,
            onValueChange = {
                val serverUrl = it.trim()
                serverViewModel.serverUrl = serverUrl
                if(rememberServer) {
                    val serverConst = SettingsConstants.SERVER_URL.toString()
                    settings[serverConst] = serverUrl
                }
            }
        )
        Row {
            Checkbox(
                modifier = Modifier.padding(16.dp),
                checked = rememberServer,
                onCheckedChange = {
                    rememberServer = it
                    val rememberServerConst = SettingsConstants.REMEMBER_SERVER.toString()
                    val serverConst = SettingsConstants.SERVER_URL.toString()
                    settings[rememberServerConst] = rememberServer
                    if(rememberServer) {
                        settings[serverConst] = serverViewModel.serverUrl
                    } else {
                        settings[serverConst] = ""
                    }
                }
            )
            Text("Remember server?", modifier = Modifier.padding(16.dp))
        }
        Spacer(modifier = Modifier.height(15.dp))
        OutlinedTextField(
            modifier = Modifier
                .focusRequester(focusRequesterUsername)
                .onKeyEvent { keyEvent ->
                    handleKeyEvents(navigator, keyEvent,
                        onTab = {
                            focusRequesterPassword.requestFocus()
                        }
                    )
                },
            label = {
                Text("Username")
            },
            value = authenticationViewModel.username,
            onValueChange = {
                val username = it.trim()
                authenticationViewModel.username = username
                if(rememberUserPassword) {
                    val usernameConst = SettingsConstants.USERNAME.toString()
                    settings[usernameConst] = username
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier
                .focusRequester(focusRequesterPassword)
                .onKeyEvent { keyEvent ->
                    handleKeyEvents(navigator, keyEvent,
                        onTab = {
                            focusRequesterUrl.requestFocus()
                        }
                    )
                },
            label = {
                Text("Password")
            },
            value = authenticationViewModel.password,
            onValueChange = {
                val password = it.trim()
                authenticationViewModel.password = password
                if(rememberUserPassword) {
                    val passwordConst = SettingsConstants.PASSWORD.toString()
                    settings[passwordConst] = password
                }
            },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible.value)
                    Icons.Filled.VisibilityOff
                else Icons.Filled.Visibility

                // Localized description for accessibility services
                val description = if (passwordVisible.value) "Hide password" else "Show password"

                // Toggle button to hide or display password
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(imageVector = image, description)
                }
            }
        )
        Row {
            Checkbox(
                modifier = Modifier.padding(16.dp),
                checked = rememberUserPassword,
                onCheckedChange = {
                    rememberUserPassword = it
                    val rememberCredentials = SettingsConstants.REMEMBER_CREDENTIALS.toString()
                    val username = SettingsConstants.USERNAME.toString()
                    val password = SettingsConstants.PASSWORD.toString()
                    settings[rememberCredentials] = rememberUserPassword
                    if(rememberUserPassword) {
                        settings[username] = authenticationViewModel.username
                        settings[password] = authenticationViewModel.password
                    } else {
                        settings[username] = ""
                        settings[password] = ""
                    }
                }
            )
            Text("Remember Credentials?", modifier = Modifier.padding(16.dp))
        }
        Row {
            val formIsFilled = authenticationViewModel.username.isNotEmpty()
                    && authenticationViewModel.password.isNotEmpty() && serverViewModel.serverUrl.isNotEmpty()
            AnimatedVisibility(formIsFilled) {
                Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            logger.info {
                                "User ${authenticationViewModel.username} is trying to login."
                            }
                            try {
                                authenticationViewModel.login(navigator, serverViewModel)
                            } catch (ex: Exception) {
                                openDialog.value = true
                                errorMsg.value = ex.message ?: "Cannot login!"
                            }
                        }
                        isLoading = false
                    }
                ) {
                    Text("Login")
                }
            }
            Button(
                modifier = Modifier
                    .focusRequester(focusRequesterSettings)
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.S && keyEvent.isCtrlPressed) {
                            navigator.navigate("/settings")
                            true
                        } else {
                            false
                        }
                    },
                onClick = {
                    navigator.navigate("/settings")
                }
            ) {
                Text("Settings")
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequesterUrl.requestFocus()
        val serverConst = SettingsConstants.SERVER_URL.toString()
        serverViewModel.serverUrl = settings[serverConst] ?: "https://"

        val usernameConst = SettingsConstants.USERNAME.toString()
        val passwordConst = SettingsConstants.PASSWORD.toString()
        authenticationViewModel.username = settings[usernameConst] ?: ""
        authenticationViewModel.password = settings[passwordConst] ?: ""

        serverViewModel.checkExpiresIn()
    }

    if (openDialog.value) {
        Alert(openDialog, errorMsg.value)
    }
}

// TODO Make this general usable for other components
@OptIn(ExperimentalComposeUiApi::class)
fun handleKeyEvents(
    navigator: Navigator,
    keyEvent: KeyEvent,     // Get KeyEvents
    onTab: () -> Unit       // Handle Tab
): Boolean {
    return if (keyEvent.key == Key.S && keyEvent.isCtrlPressed) {
        navigator.navigate(Routes.SETTINGS.path)
        true
    } else if (keyEvent.key == Key.Tab && !keyEvent.isShiftPressed) {
        onTab()
        true
    } else {
        false
    }
}