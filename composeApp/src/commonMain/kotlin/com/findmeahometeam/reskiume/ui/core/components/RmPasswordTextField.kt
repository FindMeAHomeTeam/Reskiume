package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
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
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    TextField(
        modifier = modifier.then(modifier.clip(RoundedCornerShape(20.dp))),
        value = password,
        onValueChange = onValueChange,
        singleLine = true,
        label = { Text(stringResource(Res.string.password_text_field_label)) },
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
            focusedContainerColor = Color.LightGray.copy(alpha = 0.5f),
            unfocusedContainerColor = Color.LightGray.copy(alpha = 0.5f)
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