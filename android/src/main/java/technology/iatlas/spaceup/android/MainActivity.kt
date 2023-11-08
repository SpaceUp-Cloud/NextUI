package technology.iatlas.spaceup.android

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import moe.tlaster.precompose.PreComposeApp
import technology.iatlas.spaceup.common.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // Turn off the decor fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false);
        setContent{
            PreComposeApp {
                App()
            }
        }
    }
}