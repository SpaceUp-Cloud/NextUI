import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
import moe.tlaster.precompose.ui.BackDispatcher
import moe.tlaster.precompose.ui.BackDispatcherOwner
import moe.tlaster.precompose.ui.LocalBackDispatcherOwner
import moe.tlaster.precompose.ui.LocalLifecycleOwner
import moe.tlaster.precompose.ui.LocalViewModelStoreOwner
import moe.tlaster.precompose.viewmodel.ViewModelStore
import moe.tlaster.precompose.viewmodel.ViewModelStoreOwner
import technology.iatlas.spaceup.common.App
import javax.imageio.ImageIO

val profile = System.getProperty("nextui.profile") ?: ""

@OptIn(ExperimentalFoundationApi::class)
fun main() = application {
    var isDark by remember { mutableStateOf(false) }
    var isAutoMode by remember { mutableStateOf(false) }

    val isDarkMode = if(isAutoMode) {
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
        size = DpSize(700.dp, 750.dp),
        position = WindowPosition(Alignment.BottomCenter)
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
            icon = ImageIO.read(this::class.java.getResourceAsStream("/spaceup_icon.png")).toPainter(),
            onCloseRequest = {
                holder.lifecycle.currentState = Lifecycle.State.Destroyed
                exitApplication()
            },
            title = "SpaceUp-NextUI ${if(profile.isNotEmpty()) profile.uppercase() else ""}",
            theme = currentTheme,
            state = state,
            mainToolBar = {
                Row(Modifier.mainToolBarItem(Alignment.End)) {
                    if(!isAutoMode) {
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
        LocalViewModelStoreOwner provides holder,
        LocalBackDispatcherOwner provides holder,
    ) {
        content.invoke()
    }
}

private class PreComposeWindowHolder : LifecycleOwner, ViewModelStoreOwner, BackDispatcherOwner {
    override val lifecycle by lazy {
        LifecycleRegistry()
    }
    override val viewModelStore by lazy {
        ViewModelStore()
    }
    override val backDispatcher by lazy {
        BackDispatcher()
    }
}