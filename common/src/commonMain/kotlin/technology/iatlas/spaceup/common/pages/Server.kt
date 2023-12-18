package technology.iatlas.spaceup.common.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.CloudCircle
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.stateholder.LocalSavedStateHolder
import technology.iatlas.spaceup.common.model.Hostname
import technology.iatlas.spaceup.common.model.Routes
import technology.iatlas.spaceup.common.model.SettingsConstants
import technology.iatlas.spaceup.common.util.httpClient
import technology.iatlas.spaceup.common.viewmodel.ServerViewModel
import java.net.ConnectException

@OptIn(InternalAPI::class)
@Composable
fun Server() {
    val logger = KotlinLogging.logger {  }

    val settings = Settings()
    val navigator = rememberNavigator()

    val serverVersion = remember { mutableStateOf("") }
    val hostname = remember { mutableStateOf("") }

    val stateHolder = LocalSavedStateHolder.current
    val serverViewModel = koinViewModel(ServerViewModel::class) { org.koin.core.parameter.parametersOf(stateHolder) }
    val serverUrl = serverViewModel.serverUrl

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = Modifier
                .height(35.dp)
                .padding(4.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                TextLineIcon(
                    text = "Server Version: ${serverVersion.value}",
                    icon = Icons.Default.Api,
                    iconTint = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Card(
            modifier = Modifier
                .height(35.dp)
                .padding(4.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                TextLineIcon(
                    text = "Hostname: ${hostname.value}",
                    icon = Icons.Default.CloudCircle,
                    iconTint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        val client =  httpClient(settings.getString(SettingsConstants.ACCESS_TOKEN.toString(), ""))

        try {
            val responseVersion = client
                .get("${serverUrl}/api/system/version")
            val content = responseVersion.body<String>()
            logger.info { "Version response: $responseVersion" }
            if(responseVersion.status == HttpStatusCode.OK) {
                logger.info { "Server version: $content" }
                serverVersion.value = content
            } else {
                logger.error { content }
            }

            val responseHostname = client
                .get("${serverUrl}/api/system/hostname")
            val hostnameBody = responseHostname.body<Hostname>()
            if(responseVersion.status == HttpStatusCode.OK) {
                logger.info { "Hostname: $hostnameBody" }
                hostname.value = hostnameBody.hostname
            } else {
                logger.error { hostnameBody }
            }
        } catch (ex: ConnectException) {
            // unable to connect to host
            // clear server from history and remove JWT
            settings[SettingsConstants.SERVER_URL.toString()] = ""
            settings[SettingsConstants.ACCESS_TOKEN.toString()] = ""

            // logout
            navigator.navigate(Routes.LOGIN.path)
        }
    }
}

@Composable
fun TextLineIcon(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    iconRightPadding: Dp = 0.dp,
    iconLine: Int = 0,
    iconTint: Color = MaterialTheme.colorScheme.secondary
    // etc
) {
    val painter = rememberVectorPainter(image = icon)
    var lineTop = 0f
    var lineBottom = 0f
    var lineLeft = 0f
    with(LocalDensity.current) {
        val imageSize = Size(icon.defaultWidth.toPx(), icon.defaultHeight.toPx())
        val rightPadding = iconRightPadding.toPx()
        Text(
            text = text,
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight,
            onTextLayout = { layoutResult ->
                val nbLines = layoutResult.lineCount
                if (nbLines > iconLine) {
                    lineTop = layoutResult.getLineTop(iconLine)
                    lineBottom = layoutResult.getLineBottom(iconLine)
                    lineLeft = layoutResult.getLineLeft(iconLine)
                }
            },
            modifier = modifier.drawBehind {
                with(painter) {
                    translate(
                        left = lineLeft - imageSize.width - rightPadding,
                        top = lineTop + (lineBottom - lineTop) / 2 - imageSize.height / 2,
                    ) {
                        //draw(painter.intrinsicSize, colorFilter = ColorFilter.tint(iconTint))
                    }
                }
            }
        )
    }
}