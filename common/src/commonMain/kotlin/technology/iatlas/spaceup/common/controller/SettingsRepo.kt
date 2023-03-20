package technology.iatlas.spaceup.common.controller

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import okio.Path.Companion.toPath

fun createDataStore(
    coroutineScope: CoroutineScope,
    producePath: () -> String
): DataStore<Preferences> = PreferenceDataStoreFactory.createWithPath(
    corruptionHandler = null,
    migrations = emptyList(),
    scope = coroutineScope,
    produceFile = { producePath().toPath() },
)

internal const val dataStoreFileName = "spaceup-nextui.preferences_pb"