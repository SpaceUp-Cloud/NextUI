package technology.iatlas.spaceup.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@Composable
fun ErrorAlert(openDialog: MutableState<Boolean>, errorMsg: String) {
    val scroll = rememberScrollState(0)
    AlertDialog(
        backgroundColor = MaterialTheme.colorScheme.errorContainer,
        onDismissRequest = {
            openDialog.value = false
        },
        confirmButton = {
            Button(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text("Close")
            }
        },
        title = {
            Text("Error")
        },
        text = {
            val baseMsg = "An error occurred: \n%s"
            val msg = if(errorMsg.isEmpty()) baseMsg.replace(": %s", "")
            else baseMsg.replace("%s", errorMsg)
            Text(
                text = msg,
                modifier = Modifier
                    .verticalScroll(scroll)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}