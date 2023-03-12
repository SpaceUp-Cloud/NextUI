package technology.iatlas.spaceup.common.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable

@Composable
fun MyToolbar(
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    TopAppBar(
        title = { Text("Overview") },
        navigationIcon = {
            IconButton(onClick = { /* handle navigation icon click */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = actions ?: {}
    )
}