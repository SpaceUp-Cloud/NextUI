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
import technology.iatlas.spaceup.common.util.Helper.getSystemProfile

val profile = getSystemProfile()

actual fun httpClient(token: String): HttpClient {
    return HttpClient(CIO) {
        if (profile == "dev") {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
        } else {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.INFO
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
                    BearerTokens(token, "")
                }
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000 // Webbackend list, Get domains take rather long
        }
    }
}