package technology.iatlas.spaceup.common.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsView() {
    val settingsList = listOf(
        SettingItemState("Einstellung 1", "Beschreibung 1", Icons.Filled.Settings, mutableStateOf(false)) { },
        SettingItemState("Einstellung 2", "Beschreibung 2", Icons.Filled.Info, mutableStateOf(true)) {},
        SettingItemState("Einstellung 3", "Beschreibung 3", Icons.Filled.Settings, mutableStateOf(false)) {},
        SettingItemState("Einstellung 4", "Beschreibung 4", Icons.Filled.Info, mutableStateOf(true)) {},
        SettingItemState("Einstellung 5", "Beschreibung 5", Icons.Filled.Settings, mutableStateOf(false)) {}
    )

    Column {
        Settings(settingsList = settingsList)
    }
}

@Composable
fun Settings(settingsList: List<SettingItemState>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(settingsList.size) { index ->
            SettingItem(settingsList[index])
            //Divider(color = Color.LightGray)
        }
    }
}

@Composable
fun SettingItem(setting: SettingItemState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = setting.icon, contentDescription = null)
        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
            Text(text = setting.title)
            Text(text = setting.description, style = MaterialTheme.typography.bodyMedium)
        }
        when (setting.value) {
            is Boolean -> Checkbox(checked = !(setting.value as Boolean), onCheckedChange = setting.action)
        }
    }
}

data class SettingItemState(
    val title: String,
    val description: String,
    val icon: ImageVector,
    var value: Any,
    val action: (value: Any) -> Unit
)
