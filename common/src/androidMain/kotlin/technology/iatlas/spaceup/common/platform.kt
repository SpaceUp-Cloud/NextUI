package technology.iatlas.spaceup.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler

actual fun getPlatformName(): String {
    return "Android"
}

@Composable
actual fun openInBrowser(domain: String) {
    LocalUriHandler.current.openUri(domain)
}