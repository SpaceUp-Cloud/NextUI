import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.kanro.compose.jetbrains.expui.control.ActionButton
import io.kanro.compose.jetbrains.expui.control.Tooltip
import io.kanro.compose.jetbrains.expui.theme.DarkTheme
import io.kanro.compose.jetbrains.expui.theme.LightTheme
import io.kanro.compose.jetbrains.expui.window.JBWindow
import technology.iatlas.spaceup.common.App
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger
import javax.imageio.ImageIO


@OptIn(ExperimentalFoundationApi::class)
fun main() = application {
    val logger = Logger.getLogger("Desktop Main")
    var isDark by remember { mutableStateOf(false) }
    val theme = if (isDark) {
        logger.log(Level.INFO, "Set dark theme")
        DarkTheme
    } else {
        logger.log(Level.INFO, "Set light theme")
        LightTheme
    }

    val imageFile = File(this::class.java.getResource("/spaceup_icon.png").toURI())
    val image = ImageIO.read(imageFile)
    val bitmap = image.toPainter()

    JBWindow(
        icon = bitmap,
        onCloseRequest = ::exitApplication,
        title = "SpaceUp-NextUI",
        theme = theme,
        state = rememberWindowState(size = DpSize(900.dp, 700.dp)),
        mainToolBar = {
            Row(Modifier.mainToolBarItem(Alignment.End)) {
                Tooltip("Switch between dark and light mode,\ncurrently is ${if (isDark) "dark" else "light"} mode") {
                    ActionButton(
                        { isDark = !isDark }, Modifier.size(80.dp), shape = RectangleShape
                    ) {
                        /*if (isDark) {
                            //Icon("icons/darkTheme.svg")
                        } else {
                            //Icon("icons/lightTheme.svg")
                        }*/ Text("Dark/Light", color = Color.White)
                    }
                }
            }
        }
    ) {
        MaterialTheme {
            App()
        }
    }
}
