package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.secondaryGreen

@Composable
fun RmCircularProgressIndicator(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier.then(Modifier.size(64.dp)),
        color = secondaryGreen,
        trackColor = Color.White,
    )
}