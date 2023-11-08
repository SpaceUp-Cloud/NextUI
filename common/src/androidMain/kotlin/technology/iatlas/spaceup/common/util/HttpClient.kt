package technology.iatlas.spaceup.common.util

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.gson.*

actual fun httpClient(token: String): HttpClient {
    return HttpClient(Android) {
        /*install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }*/
        install(ContentNegotiation) {
            gson()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(token, "")
                }
            }
        }
        install(HttpTimeout) {
            // 60 secs
            requestTimeoutMillis = 60000 // Webbackend list, Get domains take rather long
        }
    }
}