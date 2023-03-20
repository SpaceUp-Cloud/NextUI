package technology.iatlas.spaceup.common.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.oshai.KotlinLogging
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.ui.viewModel
import technology.iatlas.spaceup.common.components.Alert
import technology.iatlas.spaceup.common.viewmodel.AuthenticationViewModel
import technology.iatlas.spaceup.common.viewmodel.ServerViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Login(navigator: Navigator) {
    /*
    Center screened components
    - SpaceUp Server
    - Username
    - Password
     */
    val logger = KotlinLogging.logger {  }
    var isLoading by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    val passwordVisible = remember { mutableStateOf(false) }
    val errorMsg = remember { mutableStateOf("") }
    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }

    val coroutineScope = rememberCoroutineScope()
    val serverViewModel = viewModel(ServerViewModel::class) {
        ServerViewModel()
    }
    val authenticationViewModel = viewModel(AuthenticationViewModel::class) {
        AuthenticationViewModel()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SpaceUp Login",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            modifier = Modifier
                .focusRequester(focusRequester1)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Tab && !keyEvent.isShiftPressed) {
                        focusRequester1.requestFocus()
                        true
                    } else {
                        false
                    }
                },
            label = {
                Text("SpaceUp-Server")
            },
            value = serverViewModel.serverUrl,
            onValueChange = {
                if(it.isBlank() || it.isEmpty()) return@TextField
                serverViewModel.serverUrl = it
            }
        )
        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            modifier = Modifier
                .focusRequester(focusRequester2)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Tab && !keyEvent.isShiftPressed) {
                        focusRequester2.requestFocus()
                        true
                    } else {
                        false
                    }
                },
            label = {
                Text("Username")
            },
            value = authenticationViewModel.username,
            onValueChange = {
                if(it.isBlank() || it.isEmpty()) return@TextField
                authenticationViewModel.username = it
            }
        )
        TextField(
            modifier = Modifier
                .focusRequester(focusRequester3)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Tab && !keyEvent.isShiftPressed) {
                        focusRequester3.requestFocus()
                        true
                    } else {
                        false
                    }
                },
            label = {
                Text("Password")
            },
            value = authenticationViewModel.password,
            onValueChange = {
                if(it.isBlank() || it.isEmpty()) return@TextField
                authenticationViewModel.password = it
            },
            visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible.value)
                    Icons.Filled.Warning
                else Icons.Filled.Check

                // Localized description for accessibility services
                val description = if (passwordVisible.value) "Hide password" else "Show password"

                // Toggle button to hide or display password
                IconButton(onClick = {passwordVisible.value = !passwordVisible.value}){
                    Icon(imageVector  = image, description)
                }
            }
        )
        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    logger.info {
                        "User ${authenticationViewModel.username} is trying to login."
                    }
                    try {
                        authenticationViewModel.login(navigator, serverViewModel)
                    }catch (ex: Exception) {
                        openDialog.value = true
                        errorMsg.value = ex.message ?: "Cannot login!"
                    }
                }
                isLoading = false
            }
        ) {
            Text("Login")
        }
        Button(
            onClick = {
                navigator.navigate("/settings")
            }
        ) {
            Text("Settings")
        }
    }

    LaunchedEffect(Unit) {
        focusRequester1.requestFocus()
    }

    if(openDialog.value) {
        Alert(openDialog, errorMsg.value)
    }
}