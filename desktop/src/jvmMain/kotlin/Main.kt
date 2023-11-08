import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.kanro.compose.jetbrains.expui.control.ActionButton
import io.kanro.compose.jetbrains.expui.control.Icon
import io.kanro.compose.jetbrains.expui.control.Tooltip
import io.kanro.compose.jetbrains.expui.theme.DarkTheme
import io.kanro.compose.jetbrains.expui.theme.LightTheme
import io.kanro.compose.jetbrains.expui.window.JBWindow
import kotlinx.coroutines.flow.distinctUntilChanged
import moe.tlaster.precompose.lifecycle.Lifecycle
import moe.tlaster.precompose.lifecycle.LifecycleOwner
import moe.tlaster.precompose.lifecycle.LifecycleRegistry
import moe.tlaster.precompose.lifecycle.LocalLifecycleOwner
import moe.tlaster.precompose.stateholder.LocalStateHolder
import moe.tlaster.precompose.stateholder.StateHolder
import moe.tlaster.precompose.ui.BackDispatcher
import moe.tlaster.precompose.ui.BackDispatcherOwner
import moe.tlaster.precompose.ui.LocalBackDispatcherOwner
import technology.iatlas.spaceup.common.App
import technology.iatlas.spaceup.common.util.Helper.getSystemProfile
import javax.imageio.ImageIO

val profile = getSystemProfile()

@OptIn(ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class)
fun main() = application {
    var isDark by remember { mutableStateOf(false) }
    var isAutoMode by remember { mutableStateOf(true) }

    val painterIcon = ImageIO.read(this::class.java.getResourceAsStream("/spaceup_icon.png")).toPainter()

    val isDarkMode = if (isAutoMode) {
        isSystemInDarkTheme()
    } else {
        isDark
    }

    val currentTheme = if (isDarkMode) {
        DarkTheme
    } else {
        LightTheme
    }

    val state = rememberWindowState(
        size = DpSize(600.dp, 650.dp),
        position = WindowPosition(Alignment.Center)
    )

    val holder = remember {
        PreComposeWindowHolder()
    }

    LaunchedEffect(Unit) {
        snapshotFlow { state.isMinimized }
            .distinctUntilChanged()
            .collect {
                holder.lifecycle.currentState = if (it) {
                    Lifecycle.State.InActive
                } else {
                    Lifecycle.State.Active
                }
            }
    }

    ProvideDesktopCompositionLocals(holder) {
        JBWindow(
            icon = painterIcon,
            onCloseRequest = {
                holder.lifecycle.currentState = Lifecycle.State.Destroyed
                exitApplication()
            },
            title = "SpaceUp-NextUI ${profile.uppercase()}",
            theme = currentTheme,
            state = state,
            mainToolBar = {
                Row(Modifier.mainToolBarItem(Alignment.End)) {
                    if (!isAutoMode) {
                        Tooltip("Switch between dark and light mode,\ncurrently is ${if (isDark) "dark" else "light"} mode") {
                            ActionButton(
                                { isDark = !isDark }, Modifier.size(40.dp), shape = RectangleShape
                            ) {
                                if (isDark) {
                                    Icon("icons/darkTheme.svg")
                                } else {
                                    Icon("icons/lightTheme.svg")
                                }
                            }
                        }
                    }
                    Tooltip("Auto dark mode: currently ${if (isDarkMode) "dark" else "light"}") {
                        Switch(
                            checked = isAutoMode,
                            onCheckedChange = {
                                isAutoMode = it
                            }
                        )
                    }
                }
            }
        ) {
            App(useDarkTheme = isDarkMode)
        }
    }
}

@Composable
private fun ProvideDesktopCompositionLocals(
    holder: PreComposeWindowHolder = remember {
        PreComposeWindowHolder()
    },
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalLifecycleOwner provides holder,
        LocalStateHolder provides holder.stateHolder,
        LocalBackDispatcherOwner provides holder,
    ) {
        content.invoke()
    }
}

private class PreComposeWindowHolder : LifecycleOwner, BackDispatcherOwner {
    override val lifecycle by lazy {
        LifecycleRegistry()
    }
    val stateHolder by lazy {
        StateHolder()
    }
    override val backDispatcher by lazy {
        BackDispatcher()
    }
}