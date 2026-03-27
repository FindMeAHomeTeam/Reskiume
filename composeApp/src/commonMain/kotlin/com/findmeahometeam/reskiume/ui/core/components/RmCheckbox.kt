package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.textColor

@Composable
fun RmCheckbox(
    label: String,
    modifier: Modifier = Modifier,
    isChecked: Boolean = false,
    onChecked: (isChecked: Boolean) -> Unit
) {
    var checked by rememberSaveable { mutableStateOf(isChecked) }

    val onCheckedChange: (Boolean) -> Unit = {
        checked = it
        onChecked(it)
    }

    Row(
        modifier = modifier.then(
            Modifier.clickable {
                onCheckedChange(!checked)
            }
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = checked,
            colors = CheckboxDefaults.colors().copy(
                checkedBoxColor = primaryGreen,
                checkedBorderColor = primaryGreen,
                uncheckedBorderColor = textColor
            ),
            onCheckedChange = onCheckedChange
        )
        Spacer(modifier = Modifier.width(5.dp))
        RmText(label)
    }
}
