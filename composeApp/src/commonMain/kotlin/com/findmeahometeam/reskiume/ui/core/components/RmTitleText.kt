package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.ui.core.textColor

@Composable
fun RmTitleText(
    text: String,
    isSectionTitle: Boolean = true,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.then(Modifier.fillMaxWidth()),
        textAlign = if (isSectionTitle) TextAlign.Start else TextAlign.Center,
        text = text,
        color = textColor,
        fontSize = if (isSectionTitle) 18.sp else 24.sp,
        fontWeight = if (isSectionTitle) FontWeight.ExtraBold else FontWeight.Black
    )
}