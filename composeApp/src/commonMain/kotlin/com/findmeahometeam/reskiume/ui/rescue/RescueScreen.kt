package com.findmeahometeam.reskiume.ui.rescue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.findmeahometeam.reskiume.ui.core.components.RmText

@Composable
fun RescueScreen() {

    Column {
        Box(modifier = Modifier.fillMaxSize().background(color = Color.Cyan),
            contentAlignment = Alignment.Center
        ) {
            RmText("Rescue screen")
        }
    }
}