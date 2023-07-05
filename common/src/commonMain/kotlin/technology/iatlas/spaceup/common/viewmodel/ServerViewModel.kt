package technology.iatlas.spaceup.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import moe.tlaster.precompose.viewmodel.ViewModel
import technology.iatlas.spaceup.common.model.SettingsConstants
import technology.iatlas.spaceup.common.util.httpClient
import kotlin.time.Duration.Companion.seconds

class ServerViewModel : ViewModel() {
    private val logger = KotlinLogging.logger { }

    // Contains base url
    var serverUrl by mutableStateOf("https://")

    // Contains the JWT for authentication
    var token by mutableStateOf(Token("", "", 0))

    // For showing how long the JWT is valid
    var expiresAsString by mutableStateOf("")

    init {
        val settings = Settings()
        serverUrl = settings[SettingsConstants.SERVER_URL.toString()] ?: ""
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun checkExpiresIn() = runBlocking {
        CoroutineScope(Dispatchers.IO.limitedParallelism(1)).launch {
            while (true) {
                if(token.expiresIn <= 0) {
                    expiresAsString = "Session timed out." // TODO use it for re-login automatically
                    delay(100)
                } else {
                    expiresAsString = "Session " +
                            "${token.expiresIn.seconds}" // sec in min
                    logger.trace { "token expires in ${token.expiresIn.seconds}" }
                    // 1 second
                    delay(1000) // somehow more accurate than Kotlin delay(...)
                    token.expiresIn = token.expiresIn - 1
                }
            }
        }
    }

    /**
     * Check if the given JWT is valid or not
     *
     * @return is valid JWT
     */
    fun validToken(): Boolean {
        // TODO me
        return true
    }

    fun client(): HttpClient {
        return httpClient(token.accessToken)
    }
}