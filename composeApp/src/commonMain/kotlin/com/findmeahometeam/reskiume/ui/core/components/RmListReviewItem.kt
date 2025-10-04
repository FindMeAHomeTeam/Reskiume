package com.findmeahometeam.reskiume.ui.core.components

import RmRatingBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RmListReviewItem(
    title: String,
    description: String,
    isEnabled: Boolean = true,
    containerColor: Color = Color.White,
    listAvatarType: RmListAvatarType,
    rating: Float,
    date: String,
    onClick: () -> Unit
) {
    Card(
        enabled = isEnabled,
        colors = CardDefaults.cardColors().copy(containerColor = containerColor),
        onClick = { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            RmAvatar(listAvatarType)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RmText(
                        text = title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    RmRatingBar(
                        rating = rating,
                        starSize = 15.dp,
                        enableDragging = false,
                        enableTapping = false
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                RmSecondaryText(
                    modifier = Modifier.fillMaxWidth(),
                    text = date,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(7.dp))
                RmText(modifier = Modifier.fillMaxWidth(), text = description)
            }
        }
    }
}

