package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.ui.core.textColor

@Composable
fun RmTextBold(
    text: String,
    textToBold: String,
    modifier: Modifier = Modifier,
    color: Color = textColor,
    fontSize: TextUnit = 14.sp,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE
) {
    val startIndex = text.indexOf(textToBold)
    val endIndex = startIndex + textToBold.length
    val annotatedBoldString: AnnotatedString = buildAnnotatedString {
        append(text)
        addStyle(
            style = SpanStyle(fontWeight = FontWeight.Bold),
            start = startIndex,
            end = endIndex
        )
    }
    Text(
        text = annotatedBoldString,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        overflow = overflow,
        maxLines = maxLines
    )
}
