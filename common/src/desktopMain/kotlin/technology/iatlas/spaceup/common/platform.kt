package technology.iatlas.spaceup.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberTrayState
import java.awt.Desktop
import java.net.URI

actual fun getPlatformName(): String {
    return "Desktop"
}

@Composable
actual fun OpenInBrowser(domain: String) {
    Desktop.getDesktop().browse(URI.create(domain))
}

@Composable
actual fun notify(title: String, body: String, type: String) {
    val trayState = rememberTrayState()
    val notifyType = when(type.uppercase()) {
        "INFO" -> Notification.Type.Info
        "ERROR" -> Notification.Type.Error
        "WARN" -> Notification.Type.Warning
        "WARNING" -> Notification.Type.Warning
        else -> {Notification.Type.Info}
    }
    trayState.sendNotification(rememberNotification(title, body, notifyType))
}