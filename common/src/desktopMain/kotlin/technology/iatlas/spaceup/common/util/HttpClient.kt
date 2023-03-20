package technology.iatlas.spaceup.common.util

//import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.gson.*

actual fun httpClient(): HttpClient {
    return HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 20000 // Webbackend list, Get domains take rather long
        }
    }
}