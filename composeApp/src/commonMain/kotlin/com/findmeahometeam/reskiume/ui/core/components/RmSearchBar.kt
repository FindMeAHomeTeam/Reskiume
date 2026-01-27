package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.textColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.search_bar_erase_input_content_description
import reskiume.composeapp.generated.resources.search_bar_search_icon_content_description
import reskiume.composeapp.generated.resources.ic_erase_input
import reskiume.composeapp.generated.resources.ic_search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RmSearchBar(
    modifier: Modifier = Modifier,
    placeholder: String,
    onSearch: (String) -> Unit
) {
    var query: String by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBarDefaults.InputField(
            modifier = modifier.then(Modifier.fillMaxWidth()),
            query = query,
            onQueryChange = {
                query = it
                onSearch(it)
            },
            onSearch = {
                focusManager.clearFocus()
            },
            expanded = false,
            onExpandedChange = { },
            placeholder = { RmText(placeholder) },
            leadingIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_search),
                    contentDescription = stringResource(Res.string.search_bar_search_icon_content_description),
                    tint = textColor
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        modifier = Modifier.size(32.dp),
                        onClick = {
                            query = ""
                            onSearch("")
                            focusManager.clearFocus()
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_erase_input),
                            contentDescription = stringResource(Res.string.search_bar_erase_input_content_description),
                            tint = textColor,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            },
            colors = inputFieldColors().copy(
                unfocusedContainerColor = backgroundColorForItems,
                focusedContainerColor = backgroundColorForItems,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = textColor
            )
        )
    }
}
