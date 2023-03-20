package technology.iatlas.spaceup.common.viewmodel

import androidx.compose.runtime.mutableStateOf
import moe.tlaster.precompose.viewmodel.ViewModel
import technology.iatlas.spaceup.common.model.Settings

class SettingsViewModel: ViewModel() {
    val settings = mutableStateOf(
        Settings(
            true,
            false,
            true,
            true)
    )
}