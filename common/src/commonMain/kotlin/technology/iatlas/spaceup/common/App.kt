package technology.iatlas.spaceup.common

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import technology.iatlas.spaceup.common.views.Home
import technology.iatlas.spaceup.common.views.Login

@Composable
@Preview
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
fun App() {
    val navigator = rememberNavigator()
    NavHost(
        navigator = navigator,
        initialRoute = "/login"
    ) {
        scene(route = "/login") {
            Login(navigator)
        }
        scene(route = "/home") {
            Home(navigator)
        }
    }
}
