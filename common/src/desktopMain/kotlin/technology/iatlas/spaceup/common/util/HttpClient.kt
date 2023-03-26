package technology.iatlas.spaceup.common.util

//import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.gson.*

val profile = System.getProperty("nextui.profile") ?: ""

actual fun httpClient(bearerToken: String): HttpClient {
    return HttpClient(CIO) {
        if(profile == "dev") {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        }
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(bearerToken, "")
                }
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 20000 // Webbackend list, Get domains take rather long
        }
    }
}