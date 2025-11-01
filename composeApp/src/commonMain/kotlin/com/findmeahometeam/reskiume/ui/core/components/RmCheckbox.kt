package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.primaryGreen

@Composable
fun RmCheckbox(
    label: String,
    onChecked: (isChecked: Boolean) -> Unit
) {
    var checked by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            colors = CheckboxDefaults.colors().copy(
                checkedBoxColor = primaryGreen,
                checkedBorderColor = primaryGreen
            ),
            onCheckedChange = {
                checked = it
                onChecked(it)
            }
        )
        Spacer(modifier = Modifier.width(5.dp))
        RmText(
            modifier = Modifier.clickable {
                checked = !checked
                onChecked(checked)
            },
            text = label
        )
    }
}
