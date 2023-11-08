package technology.iatlas.spaceup.common.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.annotations.SerializedName
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import moe.tlaster.precompose.viewmodel.ViewModel
import technology.iatlas.spaceup.common.model.Authentication
import technology.iatlas.spaceup.common.util.Helper.getSystemProfile
import technology.iatlas.spaceup.common.util.httpClient

class AuthenticationViewModel : ViewModel() {
    private val logger = KotlinLogging.logger {}
    // Credentials
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    suspend fun login(serverUrl: String): Token {
        val authentication = Authentication(username.trim(), password.trim())
        val profile = getSystemProfile()
        logger.info {
            if(profile.lowercase().contains("dev")) {
                "Authenticate with $authentication to $serverUrl"
            } else {
                "Authenticate with ${authentication.password.replace(Regex("."), "*")} to $serverUrl"
            }
        }
        if(username.isNotEmpty() && password.isNotEmpty()) {
            val response = httpClient("").post("$serverUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(authentication)
            }
            if(response.status == HttpStatusCode.OK) {
                return response.body<Token>()
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