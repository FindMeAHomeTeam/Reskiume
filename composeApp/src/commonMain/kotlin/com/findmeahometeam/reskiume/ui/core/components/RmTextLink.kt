package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.textColor

@Composable
fun RmTextLink(
    text: String,
    textToLink: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: TextUnit = 14.sp,
    onClick: () -> Unit
) {
    val startIndex = text.indexOf(textToLink)
    val endIndex = startIndex + textToLink.length
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {
        append(text)
        addStyle(
            style = SpanStyle(color = primaryGreen),
            start = startIndex,
            end = endIndex
        )
        addLink(
            clickable = LinkAnnotation.Clickable(
                tag = textToLink,
                styles = TextLinkStyles(
                    style = SpanStyle(
                        color = primaryGreen,
                        textDecoration = TextDecoration.Underline
                    )
                ),
                linkInteractionListener = {
                    onClick()
                }),
            start = startIndex,
            end = endIndex
        )
    }
    Row {
        Text(
            modifier = modifier,
            text = annotatedLinkString,
            textAlign = textAlign,
            fontWeight = fontWeight,
            fontSize = fontSize,
            color = textColor
        )
    }
}
