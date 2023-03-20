import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
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
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger
import javax.imageio.ImageIO


@OptIn(ExperimentalFoundationApi::class)
fun main() = application {
    var isDark by remember { mutableStateOf(false) }
    var isAutoMode by remember { mutableStateOf(false) }

    val isDarkMode = if(isAutoMode) {
        isSystemInDarkTheme()
    } else {
        isDark
    }

    val theme = if (isDarkMode) {
        DarkTheme
    } else {
        LightTheme
    }

    val imageFile = File(this::class.java.getResource("/spaceup_icon.png").toURI())
    val icon = ImageIO.read(imageFile).toPainter()

    val state = rememberWindowState(size = DpSize(900.dp, 700.dp))

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
            icon = icon,
            onCloseRequest = {
                holder.lifecycle.currentState = Lifecycle.State.Destroyed
                exitApplication()
            },
            title = "SpaceUp-NextUI",
            theme = theme,
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
                    Tooltip("Auto dark mode") {
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
            CustomTheme(
                darkTheme = isDarkMode
            ) {
                App()
            }
        }
    }
}

@Composable
fun CustomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val lightThemeColors = lightColors(
        primary = Color(0xFFDD0D3C),
        secondary = Color.Magenta,
        error = Color(0xFFD00036),
        background = Color.White,
    )

    val darkThemeColors = darkColors(
        primary = Color.Gray,
        secondary = Color.LightGray,
        error = Color(0xFFD00036),
        background = Color.DarkGray
    )

    MaterialTheme(
        colors = if(darkTheme) darkThemeColors else lightThemeColors,
        content = content
    )
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