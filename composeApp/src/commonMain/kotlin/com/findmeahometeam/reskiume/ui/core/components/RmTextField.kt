package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.textColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun RmTextField(
    text: String,
    label: String,
    trailingIcon: DrawableResource? = null,
    trailingIconTint: Color = textColor,
    readOnly: Boolean = false,
    isError: Boolean = false,
    singleLine: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    supportingText: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        readOnly = readOnly,
        value = text,
        label = { Text(text = label) },
        onValueChange = onValueChange,
        isError = isError,
        supportingText = supportingText,
        singleLine = singleLine,
        textStyle = textStyle,
        trailingIcon = {
            if (trailingIcon != null) {
                Icon(
                    painter = painterResource(trailingIcon),
                    tint = trailingIconTint,
                    contentDescription = null
                )
            }
        },
        colors = TextFieldDefaults.colors().copy(
            cursorColor = primaryGreen,
            focusedIndicatorColor = primaryGreen,
            focusedLabelColor = textColor,
            unfocusedLabelColor = textColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedContainerColor = backgroundColorForItems,
            unfocusedContainerColor = backgroundColorForItems
        )
    )
}
