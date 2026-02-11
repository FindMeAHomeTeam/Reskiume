package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.ic_arrow_drop_down
import reskiume.composeapp.generated.resources.ic_arrow_drop_up

@Composable
fun <T> RmDropDownMenu(
    modifier: Modifier = Modifier,
    dropDownLabel: String,
    defaultElementText: String,
    items: List<Pair<T, String>>,
    textStyleForTextField: TextStyle = LocalTextStyle.current,
    onClick: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var defaultText by remember(defaultElementText) { mutableStateOf(defaultElementText) }
    var textFieldWidth by remember { mutableStateOf(0) }
    val focusRequester = remember { FocusRequester() }

    Box(modifier = modifier) {
        RmTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onGloballyPositioned { coordinates: LayoutCoordinates ->
                    textFieldWidth = coordinates.size.width
                },
            readOnly = true,
            text = defaultText,
            label = dropDownLabel,
            singleLine = true,
            textStyle = textStyleForTextField,
            trailingIcon = if (expanded) {
                Res.drawable.ic_arrow_drop_up
            } else {
                Res.drawable.ic_arrow_drop_down
            },
            onValueChange = { }
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    expanded = !expanded
                    if (expanded) focusRequester.requestFocus()
                }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = backgroundColorForItems,
            modifier = Modifier
                .width(with(LocalDensity.current) {
                    textFieldWidth.toDp()
                })
        ) {
            items.forEach { element ->
                DropdownMenuItem(
                    text = { RmText(element.second) },
                    onClick = {
                        expanded = false
                        defaultText = element.second
                        onClick(element.first)
                    }
                )
            }
        }
    }
}
