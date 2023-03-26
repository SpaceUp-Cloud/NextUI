package technology.iatlas.spaceup.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ktor.client.*
import moe.tlaster.precompose.viewmodel.ViewModel
import technology.iatlas.spaceup.common.util.httpClient

class ServerViewModel : ViewModel() {
    // Contains base url
    var serverUrl by mutableStateOf("https://")
    // Contains the JWT for authentication
    var token by mutableStateOf(Token("", "", 0))

    fun client(): HttpClient {
        return httpClient(token.accessToken)
    }
}