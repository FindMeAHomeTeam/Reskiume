package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.textColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.ic_hidden
import reskiume.composeapp.generated.resources.ic_visible
import reskiume.composeapp.generated.resources.password_text_field_hide_password_content_description
import reskiume.composeapp.generated.resources.password_text_field_label
import reskiume.composeapp.generated.resources.password_text_field_show_password_content_description

@Composable
fun RmPasswordTextField(
    password: String,
    label: String = stringResource(Res.string.password_text_field_label),
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    OutlinedTextField(
        modifier = modifier,
        value = password,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(label) },
        visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = TextFieldDefaults.colors().copy(
            cursorColor = primaryGreen,
            focusedIndicatorColor = primaryGreen,
            focusedLabelColor = textColor,
            unfocusedLabelColor = textColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedContainerColor = backgroundColorForItems,
            unfocusedContainerColor = backgroundColorForItems
        ),
        trailingIcon = {
            IconButton(onClick = { passwordHidden = !passwordHidden }) {
                val visibilityIcon =
                    if (passwordHidden) Res.drawable.ic_visible else Res.drawable.ic_hidden
                val description =
                    if (passwordHidden) {
                        stringResource(Res.string.password_text_field_show_password_content_description)
                    } else {
                        stringResource(Res.string.password_text_field_hide_password_content_description)
                    }
                Icon(
                    painter = painterResource(visibilityIcon),
                    contentDescription = description,
                    tint = textColor
                )
            }
        }
    )
}