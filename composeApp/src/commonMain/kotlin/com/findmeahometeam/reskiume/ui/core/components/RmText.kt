package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.findmeahometeam.reskiume.ui.core.textColor

@Composable
fun RmText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(modifier = modifier, text = text, color = textColor)
}