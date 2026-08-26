package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter

@Composable
fun RmImage(
    imagePath: String,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    progressIndicatorSize: Dp = 55.dp,
    modifier: Modifier = Modifier
) {
    val painter: AsyncImagePainter = rememberAsyncImagePainter(imagePath)
    val state: AsyncImagePainter.State by painter.state.collectAsState()

    when (state) {
        is AsyncImagePainter.State.Empty,
        is AsyncImagePainter.State.Loading -> {
            RmCircularProgressIndicator(
                modifier = Modifier.size(progressIndicatorSize)
            )
        }

        is AsyncImagePainter.State.Success -> {
            Image(
                painter = painter,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier
            )
        }

        is AsyncImagePainter.State.Error -> {
            println((state as AsyncImagePainter.State.Error).result)
        }
    }
}
