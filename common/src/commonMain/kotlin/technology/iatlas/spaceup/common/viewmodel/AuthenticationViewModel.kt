package technology.iatlas.spaceup.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.annotations.SerializedName
import io.github.oshai.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.viewmodel.ViewModel
import technology.iatlas.spaceup.common.model.Authentication
import technology.iatlas.spaceup.common.util.httpClient

class AuthenticationViewModel : ViewModel() {
    private val logger = KotlinLogging.logger {}
    // Credentials
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    suspend fun login(navigator: Navigator, serverViewModel: ServerViewModel) {
        val authentication = Authentication(username.trim(), password.trim())
        logger.info {
            "Authenticate with $authentication to ${serverViewModel.serverUrl}"
        }
        if(username.isNotEmpty() && password.isNotEmpty()) {
            val response = httpClient().post("${serverViewModel.serverUrl}/login") {
                contentType(ContentType.Application.Json)
                timeout {

                }
                setBody(authentication)
            }
            if(response.status == HttpStatusCode.OK) {
                serverViewModel.token = response.body() as Token
                navigator.navigate("/home")
            } else {
                throw Exception("Something is wrong: Code ${response.status.value} Message: ${response.body<String>()}")
            }
        } else {
            throw Exception("No valid credentials set.")
        }
    }
}

data class Token(
    val username: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_in")
    var expiresIn: Int
)