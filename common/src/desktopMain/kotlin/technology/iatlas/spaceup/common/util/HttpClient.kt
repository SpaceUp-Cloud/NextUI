package technology.iatlas.spaceup.common.util

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.http.*
//import io.ktor.http.*
import kotlinx.serialization.json.Json

actual fun httpClient(): HttpClient {
    return HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer(json = Json)
            accept(ContentType.Application.Json)
        }
    }
}