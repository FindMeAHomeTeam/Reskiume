package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun RmBasicText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Black,
    maxLines: Int = Int.MAX_VALUE,
    textStyle: TextStyle = TextStyle.Default,
    minFontSize: TextUnit = TextAutoSizeDefaults.MinFontSize,
    maxFontSize: TextUnit = TextAutoSizeDefaults.MaxFontSize,
    stepSize: TextUnit = 0.25.sp,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    BasicText(
        modifier = modifier,
        text = text,
        maxLines = maxLines,
        color = { color },
        style = textStyle,
        autoSize = TextAutoSize.StepBased(
            minFontSize = minFontSize,
            maxFontSize = maxFontSize,
            stepSize = stepSize
        ),
        onTextLayout = onTextLayout
    )
}
