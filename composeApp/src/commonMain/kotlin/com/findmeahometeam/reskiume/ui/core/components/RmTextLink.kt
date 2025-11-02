package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.textColor

@Composable
fun RmTextLink(text: String, textToLink: String, onClick: () -> Unit) {

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
            text = annotatedLinkString,
            color = textColor
        )
    }
}
