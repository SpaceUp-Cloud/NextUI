package technology.iatlas.spaceup.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun FullscreenCircularLoader(isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        // NOP do nothing then
                    })
                }
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.secondary,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}