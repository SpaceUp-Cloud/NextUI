package technology.iatlas.spaceup.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import moe.tlaster.precompose.stateholder.SavedStateHolder
import moe.tlaster.precompose.viewmodel.ViewModel
import technology.iatlas.spaceup.common.model.SettingsConstants

class ServerViewModel(savedStateHolder: SavedStateHolder) : ViewModel() {
    private val logger = KotlinLogging.logger { }

    var serverUrl by mutableStateOf("")

    // Contains the JWT for authentication
    private val _token = MutableStateFlow(savedStateHolder.consumeRestored("token") as String? ?: "")
    val token: StateFlow<String> = _token

    // For showing how long the JWT is valid
    var expiresAsString by mutableStateOf("")

    init {
        val settings = Settings()
        serverUrl = settings[SettingsConstants.SERVER_URL.toString()] ?: ""

        savedStateHolder.registerProvider("token") {
            token.value
        }
    }

    fun updateToken(token: String) {
        logger.info { "Update token: $token" }
        val successful = _token.tryEmit(token)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun checkExpiresIn() = runBlocking {
        CoroutineScope(Dispatchers.IO.limitedParallelism(1)).launch {
            /*while (true) {
                if(token.expiresIn <= 0) {
                    expiresAsString = "Session timed out." // TODO use it for re-login automatically
                    delay(100)
                } else {
                    expiresAsString = "Session " +
                            "${token.expiresIn.seconds}" // sec in min
                    logger.trace { "token expires in ${token.expiresIn.seconds}" }
                    // 1 second
                    delay(1000) // somehow more accurate than Kotlin delay(...)
                    token.expiresIn -= 1
                }

            }*/
        }
    }

    /**
     * Check if the given JWT is valid or not
     *
     * @return is valid JWT
     */
    suspend fun validToken(): Boolean {
        return false //token.expiresIn >= 0
    }
}