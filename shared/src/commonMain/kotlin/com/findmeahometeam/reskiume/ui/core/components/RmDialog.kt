package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.gray
import com.findmeahometeam.reskiume.ui.core.secondaryTextColor

@Composable
fun RmDialog(
    emoji: String,
    title: String,
    message: String,
    allowMessage: String,
    denyMessage: String = "",
    onClickAllow: () -> Unit,
    onClickDeny: () -> Unit = {}
) {
    Dialog(onDismissRequest = onClickDeny) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors().copy(containerColor = backgroundColor),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RmText(
                    text = emoji,
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                RmText(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                RmText(
                    text = message,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = secondaryTextColor,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                RmButton(
                    text = allowMessage,
                    onClick = onClickAllow
                )
                if (denyMessage.isNotBlank()) {
                    RmButton(
                        text = denyMessage,
                        onClick = onClickDeny,
                        containerColor = gray
                    )
                }
            }
        }
    }
}
