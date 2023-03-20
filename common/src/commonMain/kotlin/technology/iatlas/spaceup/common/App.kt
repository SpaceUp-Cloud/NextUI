package technology.iatlas.spaceup.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import moe.tlaster.precompose.navigation.BackStackEntry
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.ui.LocalViewModelStoreOwner
import moe.tlaster.precompose.viewmodel.ViewModelStoreOwner
import technology.iatlas.spaceup.common.views.Home
import technology.iatlas.spaceup.common.views.Login
import technology.iatlas.spaceup.common.views.SettingsView

@Composable
fun App() {
    val navigator = rememberNavigator()
    val viewModelStoreOwner = LocalViewModelStoreOwner.current

    NavHost(
        navigator = navigator,
        initialRoute = "/login"
    ) {
        scene(route = "/login", viewModelStoreOwner = viewModelStoreOwner) {
            Login(navigator)
        }
        scene(route = "/home", viewModelStoreOwner = viewModelStoreOwner) {
            Home(navigator)
        }
        scene(route = "/settings", viewModelStoreOwner = viewModelStoreOwner) {
            SettingsView(navigator)
        }
    }
}

fun RouteBuilder.scene(
    viewModelStoreOwner: ViewModelStoreOwner,
    route: String,
    deepLinks: List<String> = emptyList(),
    navTransition: NavTransition? = null,
    content: @Composable (BackStackEntry) -> Unit,
) {
    scene(route = route, deepLinks = deepLinks, navTransition = navTransition) {
        CompositionLocalProvider(
            LocalViewModelStoreOwner provides viewModelStoreOwner,
        ) {
            content(it)
        }
    }
}