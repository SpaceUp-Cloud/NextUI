package technology.iatlas.spaceup.common

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.RouteBuilder
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import moe.tlaster.precompose.ui.LocalViewModelStoreOwner
import moe.tlaster.precompose.ui.viewModel
import moe.tlaster.precompose.viewmodel.ViewModelStoreOwner
import technology.iatlas.spaceup.common.model.Routes
import technology.iatlas.spaceup.common.theme.AppTheme
import technology.iatlas.spaceup.common.viewmodel.ServerViewModel
import technology.iatlas.spaceup.common.views.Home
import technology.iatlas.spaceup.common.views.Login
import technology.iatlas.spaceup.common.views.SettingsView

@Composable
fun App(useDarkTheme: Boolean = isSystemInDarkTheme()) {
    AppTheme(useDarkTheme = useDarkTheme) {
        val navigator = rememberNavigator()
        val viewModelStoreOwner = LocalViewModelStoreOwner.current

        val serverViewModel = viewModel(ServerViewModel::class) {
            ServerViewModel()
        }

        NavHost(
            navigator = navigator,
            initialRoute = if(serverViewModel.token.accessToken.isNotEmpty()) Routes.HOME.path else Routes.LOGIN.path
        ) {
            scene(route = Routes.LOGIN.path, navigation = navigator, viewModelStoreOwner = viewModelStoreOwner) {
                Login(it)
            }
            scene(route = Routes.LOGOUT.path, navigation = navigator, viewModelStoreOwner = viewModelStoreOwner) {
                Login(it)
            }
            sceneWithDrawer(route = Routes.HOME.path, navigation = navigator, viewModelStoreOwner = viewModelStoreOwner) {
                Home(it)
            }
            sceneWithDrawer(route = Routes.SETTINGS.path, navigation = navigator, viewModelStoreOwner = viewModelStoreOwner) {
                SettingsView(it)
            }
        }
    }
}

fun RouteBuilder.scene(
    navigation: Navigator,
    viewModelStoreOwner: ViewModelStoreOwner,
    route: String,
    deepLinks: List<String> = emptyList(),
    navTransition: NavTransition? = null,
    content: @Composable (navigation: Navigator) -> Unit,
) {
    scene(route = route, deepLinks = deepLinks, navTransition = navTransition) {
        CompositionLocalProvider(
            LocalViewModelStoreOwner provides viewModelStoreOwner,
        ) {
            content(navigation)
        }
    }
}

fun RouteBuilder.sceneWithDrawer(
    navigation: Navigator,
    viewModelStoreOwner: ViewModelStoreOwner,
    route: String,
    deepLinks: List<String> = emptyList(),
    navTransition: NavTransition? = null,
    content: @Composable (navigation: Navigator) -> Unit,
) {
    scene(route = route, deepLinks = deepLinks, navTransition = navTransition) {
        CompositionLocalProvider(
            LocalViewModelStoreOwner provides viewModelStoreOwner,
        ) {
            Drawer(navigation) {
                content(navigation)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Drawer(
    navigation: Navigator,
    content: @Composable (navigation: Navigator) -> Unit
) {
    val coroutine = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val serverViewModel = viewModel(ServerViewModel::class) {
        ServerViewModel()
    }

    val drawerList = Routes.values().toList()
    ModalNavigationDrawer(
        content = {
            Scaffold(
                //modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        modifier = Modifier
                            .background(
                                alpha = 1.0f,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Gray,
                                        Color(	0, 128, 128), // Teal
                                        Color.Gray
                                    ),
                                    //startX = 10.0f,
                                    //endX = 20.0f
                                )
                            )
                            .padding(0.dp),
                        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
                        title = { Text("NextUI") },
                        navigationIcon = {
                            IconButton(onClick = {
                                coroutine.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = {

                            }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    )
                }
            ) {
                Column(modifier = Modifier
                    //.fillMaxSize()
                    .padding(top = it.calculateTopPadding())
                ) {
                    content.invoke(navigation)
                }

            }
        },
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier
                    .width(320.dp)
                    .background(MaterialTheme.colorScheme.onPrimary)
            ) {
                if(serverViewModel.token.accessToken.isNotEmpty()) {
                    Card(
                        shape = NavShape(0.dp, 1.0f),
                        elevation = CardDefaults.elevatedCardElevation(),
                        modifier = Modifier
                            .height(30.dp)
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                    ) {
                        Text(
                            serverViewModel.serverUrl,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                    Card(
                        shape = NavShape(0.dp, 1.0f),
                        elevation = CardDefaults.elevatedCardElevation(),
                        modifier = Modifier
                            .height(30.dp)
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                    ) {
                        Text(
                            serverViewModel.expiresAsString,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                LazyColumn(
                    modifier = Modifier
                        .width(240.dp)
                        .padding(start = 24.dp, top = 8.dp)
                        .fillMaxHeight()
                    //.padding(start = 24.dp, top = 48.dp),
                ) {
                    drawerList.forEach {
                        val route = it.path
                        val menuItem = it.title
                        val drawerBehavior = it.drawerBehavior

                        it.apply {
                            if (drawerBehavior.isVisible) {
                                item {
                                    Spacer(Modifier.height(4.dp))
                                    TextButton(
                                        onClick = {
                                            try {
                                                navigation.navigate(route)
                                            } catch (ex: IllegalStateException) {
                                                // NOP Do nothing currently if there is no route
                                            }
                                        }
                                    ) {
                                        Text(
                                            menuItem,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    if (drawerBehavior.hasAfterDivider) {
                                        Divider()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
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