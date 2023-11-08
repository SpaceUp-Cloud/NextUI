package technology.iatlas.spaceup.common.util

import io.ktor.client.*

expect fun httpClient(token: String): HttpClient