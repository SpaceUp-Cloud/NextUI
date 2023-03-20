package technology.iatlas.spaceup.common.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun SettingsView(navigator: Navigator) {
    val settingsList = listOf(
        SettingItem("Einstellung 1", "Beschreibung 1", Icons.Filled.Settings, true) {},
        SettingItem("Einstellung 2", "Beschreibung 2", Icons.Filled.Info, false) {},
        SettingItem("Einstellung 3", "Beschreibung 3", Icons.Filled.Settings, true) {},
        SettingItem("Einstellung 4", "Beschreibung 4", Icons.Filled.Info, false) {},
        SettingItem("Einstellung 5", "Beschreibung 5", Icons.Filled.Settings, true) {}
    )

    Column {
        Button({navigator.goBack()}) { Text("Go back") }
        Settings(settingsList = settingsList)
    }
}

@Composable
fun Settings(settingsList: List<SettingItem>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(settingsList.size) { index ->
            SettingItem(settingsList[index])
            Divider(color = Color.LightGray)
        }
    }
}

@Composable
fun SettingItem(setting: SettingItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = setting.icon, contentDescription = null)
        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
            Text(text = setting.title)
            Text(text = setting.description, style = MaterialTheme.typography.caption)
        }
        Checkbox(checked = setting.value, onCheckedChange = setting.action)
    }
}

data class SettingItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    var value: Boolean,
    val action: (value: Boolean) -> Unit
)
