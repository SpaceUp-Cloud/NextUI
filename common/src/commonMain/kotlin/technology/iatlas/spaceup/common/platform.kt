package technology.iatlas.spaceup.common

import androidx.compose.runtime.Composable

expect fun getPlatformName(): String

@Composable
expect fun OpenInBrowser(domain: String)

@Composable
expect fun notify(title: String, body: String, type: String = "INFO")