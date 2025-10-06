package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.ui.core.primaryGreen

@Composable
fun RmButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors()
            .copy(
                containerColor = primaryGreen,
                disabledContainerColor = if (isSystemInDarkTheme()) {
                    Color.LightGray.copy(alpha = 0.2f)
                } else {
                    Color.Gray.copy(alpha = 0.2f)
                }
            ),
        onClick = onClick
    ) {
        RmText(
            modifier = Modifier.fillMaxWidth(),
            text = text,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            color = Color.White
        )
    }
}