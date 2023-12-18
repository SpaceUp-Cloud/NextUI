package technology.iatlas.spaceup.common.pages

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.russhwolf.settings.Settings
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.*
import io.ktor.client.request.*
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.stateholder.LocalSavedStateHolder
import org.koin.core.parameter.parametersOf
import technology.iatlas.spaceup.common.model.SettingsConstants
import technology.iatlas.spaceup.common.util.httpClient
import technology.iatlas.spaceup.common.viewmodel.ServerViewModel
import kotlin.math.atan2
import kotlin.math.min

@Composable
fun DiskPlot() {
    // TODO use real data from API

    val logger = KotlinLogging.logger {  }

    val settings = Settings()
    val diskUsage = remember { mutableStateOf(Disk("", 0F, "", 0F)) }

    val stateHolder = LocalSavedStateHolder.current
    val serverViewModel = koinViewModel(ServerViewModel::class) { parametersOf(stateHolder) }
    val serverUrl = serverViewModel.serverUrl

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                text = "Disk Plot"
            )
            DonutChart(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                colors = listOf(MaterialTheme.colorScheme.inversePrimary, MaterialTheme.colorScheme.secondary),
                disk = diskUsage.value
            )
        }
    }

    LaunchedEffect(Unit) {
        val client =  httpClient(settings.getString(SettingsConstants.ACCESS_TOKEN.toString(), ""))
        try {
            val response = client.get("$serverUrl/api/system/disk")
            diskUsage.value = response.body<Disk>()
        } catch (e: Exception) {
            logger.error { e }
        }
    }
}

data class Disk(
    val space: String,
    val spacePercentage: Float,
    val quota: String,
    val availableQuota: Float
)

/**
 * Component for creating Donut Chart
 * Slices are painted clockwise
 * e.g. 1st input value starts from top to the right, etc
 */

private const val animationDuration = 800
private const val chartDegrees = 360f
private const val emptyIndex = -1
private val defaultSliceWidth = 12.dp
private val defaultSlicePadding = 5.dp
private val defaultSliceClickPadding = 8.dp

@Composable
internal fun DonutChart(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    //inputValues: List<Float>,
    disk: Disk,
    sliceWidthDp: Dp = defaultSliceWidth,
    slicePaddingDp: Dp = defaultSlicePadding,
    sliceClickPaddingDp: Dp = defaultSliceClickPadding,
    animated: Boolean = true
) {

    val textMeasurer = rememberTextMeasurer()
    val maxDiskColor = MaterialTheme.colorScheme.primary
    val usedDiskSpaceColor = MaterialTheme.colorScheme.secondary
    val freeDiskSpaceColor = MaterialTheme.colorScheme.inverseSurface

    // disk text
    val text = buildAnnotatedString {
        // Max disk space
        withStyle(
            style = SpanStyle(
                color = maxDiskColor,
                fontSize = 22.sp
            )
        ) {
            append("Max disk quota:\n")
        }
        withStyle(
            style = SpanStyle(
                color = maxDiskColor,
                fontSize = 18.sp
            )
        ) {
            append("${disk.quota}\n")
            append("\n")
        }

        // Used disk space
        withStyle(
            style = SpanStyle(
                color = usedDiskSpaceColor,
                fontSize = 22.sp
            )
        ) {
            append("Used disk space:\n")
        }
        withStyle(
            style = SpanStyle(
                color = usedDiskSpaceColor,
                fontSize = 18.sp
            )
        ) {
            append("${disk.space}\n")
        }
        // Free disk space
        withStyle(
            style = SpanStyle(
                color = freeDiskSpaceColor,
                fontSize = 22.sp
            )
        ) {
            append("Free disk space: \n")
        }
        withStyle(
            style = SpanStyle(
                color = freeDiskSpaceColor,
                fontSize = 18.sp
            )
        ) {
            val regexSplit = "(\\d+)(\\D+)".toRegex()
            val maxDiskMatcher = regexSplit.find(disk.quota)
            val spaceDiskMatcher = regexSplit.find(disk.space)

            if(maxDiskMatcher != null && spaceDiskMatcher != null) {
                val (maxDiskValue, maxDiskUnit) = maxDiskMatcher.destructured
                val (spaceDiskMatcherValue, _) = spaceDiskMatcher.destructured
                val result = maxDiskValue.toInt().minus(spaceDiskMatcherValue.toInt())
                append("${result}$maxDiskUnit\n")
            } else {
                append("0B\n")
            }
        }
    }

    // calculate each input percentage
    val inputValues = listOf(disk.spacePercentage, disk.availableQuota)
    val proportions = inputValues.toPercent()

    // calculate each input slice degrees
    val angleProgress = proportions.map { prop ->
        chartDegrees * prop / 100
    }

    // start drawing clockwise (top to right)
    var startAngle = 270f

    // used for animating each slice
    val pathPortion = remember {
        Animatable(initialValue = 0f)
    }

    // clicked slice in chart
    var clickedItemIndex by remember {
        mutableStateOf(emptyIndex)
    }

    // calculate each slice end point in degrees, for handling click position
    val progressSize = mutableListOf<Float>()

    LaunchedEffect(angleProgress) {
        progressSize.add(angleProgress.first())
        for (x in 1 until angleProgress.size) {
            progressSize.add(angleProgress[x] + progressSize[x - 1])
        }
    }

    val density = LocalDensity.current

    //convert dp values to pixels
    val sliceWidthPx = with(density) { sliceWidthDp.toPx() }
    val slicePaddingPx = with(density) { slicePaddingDp.toPx() }
    val sliceClickPaddingPx = with(density) { sliceClickPaddingDp.toPx() }

    // slice width when clicked
    val selectedSliceWidth = sliceWidthPx + sliceClickPaddingPx

    // animate chart slices on composition
    LaunchedEffect(inputValues) {
        pathPortion.animateTo(1f, animationSpec = tween(if (animated) animationDuration else 0))
    }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {

        val canvasSize = min(constraints.maxWidth, constraints.maxHeight)
        val padding = canvasSize * slicePaddingPx / 100f
        val size = Size(canvasSize.toFloat() - padding, canvasSize.toFloat() - padding)
        val canvasSizeDp = with(density) { canvasSize.toDp() }

        Canvas(
            modifier = Modifier
                .size(canvasSizeDp)
                .align(Alignment.Center)
                /*.pointerInput(inputValues) {

                    detectTapGestures { offset ->
                        val clickedAngle = touchPointToAngle(
                            width = canvasSize.toFloat(),
                            height = canvasSize.toFloat(),
                            touchX = offset.x,
                            touchY = offset.y,
                            chartDegrees = chartDegrees
                        )
                        progressSize.forEachIndexed { index, item ->
                            if (clickedAngle <= item) {
                                clickedItemIndex = index
                                return@detectTapGestures
                            }
                        }
                    }
                },*/
        ) {

            angleProgress.forEachIndexed { index, angle ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = angle * pathPortion.value,
                    useCenter = false,
                    size = size,
                    style = Stroke(width = if (clickedItemIndex == index) selectedSliceWidth else sliceWidthPx),
                    topLeft = Offset(padding / 2, padding / 2),
                )
                startAngle += angle
            }

            val canvasWidth = size.width
            val canvasHeight = size.height
            val textLayoutResult = textMeasurer.measure(text)
            val textSize = textLayoutResult.size

            drawText(
                textMeasurer = textMeasurer,
                text = text,
                topLeft = Offset(
                    (canvasWidth - textSize.width) / 2f,
                    (canvasHeight - textSize.height) / 2f
                )
            )

        }
    }

}

internal fun List<Float>.toPercent(): List<Float> {
    return this.map { item ->
        item * 100 / this.sum()
    }
}

internal fun touchPointToAngle(
    width: Float,
    height: Float,
    touchX: Float,
    touchY: Float,
    chartDegrees: Float
): Double {
    val x = touchX - (width * 0.5f)
    val y = touchY - (height * 0.5f)
    var angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble()) + Math.PI / 2)
    angle = if (angle < 0) angle + chartDegrees else angle
    return angle
}