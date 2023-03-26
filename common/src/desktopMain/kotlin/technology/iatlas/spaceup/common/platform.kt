package technology.iatlas.spaceup.common

import java.awt.Desktop
import java.net.URI

actual fun getPlatformName(): String {
    return "Desktop"
}

actual fun openInBrowser(domain: String) {
    Desktop.getDesktop().browse(URI.create(domain))
}