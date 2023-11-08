package technology.iatlas.spaceup.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import io.github.nefilim.kjwt.JWT
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import technology.iatlas.spaceup.common.components.Drawer
import technology.iatlas.spaceup.common.model.Routes
import technology.iatlas.spaceup.common.model.SettingsConstants
import technology.iatlas.spaceup.common.pages.DomainsView
import technology.iatlas.spaceup.common.theme.AppTheme
import technology.iatlas.spaceup.common.views.HomeView
import technology.iatlas.spaceup.common.views.Login
import technology.iatlas.spaceup.common.views.SettingsView
import java.util.*

@Composable
fun App(useDarkTheme: Boolean = isSystemInDarkTheme()) {
    AppTheme(useDarkTheme = useDarkTheme) {
        val navigator = rememberNavigator()

        val settings = Settings()
        val accesstoken = settings.getString(SettingsConstants.ACCESS_TOKEN.toString(), "")
        var startPath by remember { mutableStateOf(Routes.LOGIN.path) }

        var isValidJWT = false
        JWT.decode(accesstoken).tap { d ->
            d.expiresAt().tap { isValidJWT = it.isAfter(Date().toInstant()) }
        }
        startPath = if(isValidJWT) Routes.HOME.path else Routes.LOGIN.path

        NavHost(
            navigator = navigator,
            initialRoute = startPath
        ) {
            scene(route = Routes.LOGIN.path, navigation = navigator) {
                Login(it)
            }
            scene(route = Routes.LOGOUT.path, navigation = navigator) {
                settings[SettingsConstants.ACCESS_TOKEN.toString()] = ""
                Login(it)
            }
            sceneWithDrawer(route = Routes.SETTINGS.path, navigation = navigator, canGoBack = true) {
                SettingsView()
            }
            sceneWithDrawer(
                route = Routes.HOME.path, navigation = navigator, canGoBack = false
            ) {
                HomeView()
            }
            sceneWithDrawer(
                route = Routes.DOMAINS.path, navigation = navigator, canGoBack = true
            ) {
                DomainsView()
            }
        }
    }
}

fun RouteBuilder.scene(
    navigation: Navigator,
    route: String,
    deepLinks: List<String> = emptyList(),
    navTransition: NavTransition? = null,
    content: @Composable (navigation: Navigator) -> Unit,
) {
    scene(route = route, deepLinks = deepLinks, navTransition = navTransition) {
        content(navigation)
    }
}

fun RouteBuilder.sceneWithDrawer(
    navigation: Navigator,
    route: String,
    deepLinks: List<String> = emptyList(),
    navTransition: NavTransition? = null,
    canGoBack: Boolean,
    content: @Composable (navigation: Navigator) -> Unit,

    ) {
    scene(route = route, deepLinks = deepLinks, navTransition = navTransition) {
        Drawer(navigation, canGoBack) {
            content(navigation)
        }
    }
}

class NavShape(
    private val widthOffset: Dp,
    private val scale: Float
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(
            Rect(
                Offset.Zero,
                Offset(
                    size.width * scale + with(density) { widthOffset.toPx() },
                    size.height
                )
            )
        )
    }
}