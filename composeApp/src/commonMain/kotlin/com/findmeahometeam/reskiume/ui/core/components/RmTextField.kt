package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.textColor

@Composable
fun RmTextField(
    text: String,
    label: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = modifier.then(modifier.clip(RoundedCornerShape(20.dp))),
        value = text,
        label = { Text(label) },
        onValueChange = onValueChange,
        colors = TextFieldDefaults.colors().copy(
            cursorColor = primaryGreen,
            focusedIndicatorColor = primaryGreen,
            focusedLabelColor = textColor,
            unfocusedLabelColor = textColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedContainerColor = Color.LightGray.copy(alpha = 0.5f),
            unfocusedContainerColor = Color.LightGray.copy(alpha = 0.5f)
        )
    )
}