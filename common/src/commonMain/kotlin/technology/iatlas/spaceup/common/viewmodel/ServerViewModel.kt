package technology.iatlas.spaceup.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import moe.tlaster.precompose.viewmodel.ViewModel

class ServerViewModel : ViewModel() {
    // Contains base url
    var serverUrl by mutableStateOf("")
    // Contains the JWT for authentication
    var token by mutableStateOf(Token("", "", 0))
}