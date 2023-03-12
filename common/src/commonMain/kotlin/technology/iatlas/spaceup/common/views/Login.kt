package technology.iatlas.spaceup.common.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.ui.viewModel
import technology.iatlas.spaceup.common.components.Alert
import technology.iatlas.spaceup.common.viewmodel.AuthenticationViewModel
import technology.iatlas.spaceup.common.viewmodel.ServerViewModel

@Composable
@Preview
fun Login(navigator: Navigator) {
    /*
    Center screened components
    - SpaceUp Server
    - Username
    - Password
     */
    var isLoading by remember { mutableStateOf(false) }
    val openDialog = remember { mutableStateOf(false) }
    val passwordVisible = remember { mutableStateOf(false) }
    val errorMsg = remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val serverViewModel = viewModel(ServerViewModel::class) {
        ServerViewModel()
    }
    val authenticationViewModel = viewModel(AuthenticationViewModel::class) {
        AuthenticationViewModel(serverViewModel)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "SpaceUp Login",
            style = MaterialTheme.typography.h6
        )
        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            label = {
                Text("SpaceUp-Server")
            },
            value = serverViewModel.serverUrl,
            onValueChange = {
                serverViewModel.serverUrl = it
            }
        )
        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            label = {
                Text("Username")
            },
            value = authenticationViewModel.username,
            onValueChange = {
                authenticationViewModel.username = it
            }
        )
        TextField(
            label = {
                Text("Password")
            },
            value = authenticationViewModel.password,
            onValueChange = {
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
                    try {
                        authenticationViewModel.login(navigator)
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
    }

    if(openDialog.value) {
        Alert(openDialog, errorMsg)
    }
}