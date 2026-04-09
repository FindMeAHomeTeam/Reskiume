package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Wraps an [onClick] lambda with another one that supports debouncing. The default deboucing time
 * is 1000ms.
 *
 * @author https://gist.github.com/leonardoaramaki/153b27eb5325f878ad4bb7ffe540c2ef
 * @return debounced onClick
 */
@OptIn(ExperimentalTime::class)
@Composable
inline fun rmDebouncer(
    crossinline onClick: () -> Unit,
    debounceTime: Long = 1000L
): () -> Unit {

    var lastTimeClicked by remember { mutableStateOf(0L) }
    val onClickLambda: () -> Unit = {
        val now = Clock.System.now().toEpochMilliseconds()
        if (now - lastTimeClicked > debounceTime) {
            onClick()
        }
        lastTimeClicked = now
    }
    return onClickLambda
}
