package technology.iatlas.spaceup.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.stateholder.LocalSavedStateHolder
import org.koin.core.parameter.parametersOf
import technology.iatlas.spaceup.common.NavShape
import technology.iatlas.spaceup.common.model.Routes
import technology.iatlas.spaceup.common.viewmodel.ServerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Drawer(
    navigation: Navigator,
    canGoBack: Boolean = true,
    content: @Composable (navigation: Navigator) -> Unit
) {
    val coroutine = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val stateHolder = LocalSavedStateHolder.current
    val serverViewModel = koinViewModel(ServerViewModel::class) { parametersOf(stateHolder) }

    val serverUrl = serverViewModel.serverUrl
    val token = serverViewModel.token.value

    val drawerList = Routes.entries
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
                                        Color(0, 128, 128), // Teal
                                        Color.Gray
                                    ),
                                    //startX = 10.0f,
                                    //endX = 20.0f
                                )
                            )
                            .padding(0.dp),
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        title = { Text("NextUI") },
                        navigationIcon = {
                            IconButton(onClick = {
                                coroutine.launch {
                                    if (canGoBack) {
                                        navigation.popBackStack()
                                    } else {
                                        drawerState.open()
                                    }
                                }
                            }) {
                                if (canGoBack) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                                } else {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                                }
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
                Column(
                    modifier = Modifier
                        //.fillMaxSize()
                        .padding(top = it.calculateTopPadding())
                ) {
                    content.invoke(navigation)
                }

            }
        },
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .width(320.dp)
                ) {
                    Card(
                        shape = NavShape(0.dp, 1.0f),
                        elevation = CardDefaults.elevatedCardElevation(),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                    ) {
                        Text(
                            serverUrl,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                    if (token.isNotEmpty()) {
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
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(10.dp, 0.dp)
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
        }
    )
}
