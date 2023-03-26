package technology.iatlas.spaceup.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Alert(openDialog: MutableState<Boolean>, errorMsg: String) {
    AlertDialog(
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
            Text(msg)
        },
        modifier = Modifier.fillMaxWidth()
    )
}