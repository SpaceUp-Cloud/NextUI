package technology.iatlas.spaceup.common.views

import androidx.compose.runtime.Composable
import technology.iatlas.spaceup.common.pages.DiskPlot
import technology.iatlas.spaceup.common.pages.Server

@Composable
fun HomeView() {
    Server()
    DiskPlot()
}
