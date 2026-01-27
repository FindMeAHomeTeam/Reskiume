package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.textColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.ic_erase_input
import reskiume.composeapp.generated.resources.ic_search
import reskiume.composeapp.generated.resources.search_bar_erase_input_content_description
import reskiume.composeapp.generated.resources.search_bar_search_icon_content_description

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Enum<T>> RmSearchBarWithSuggestions(
    textFieldState: TextFieldState,
    placeholder: String,
    modifier: Modifier = Modifier,
    onFocusChanged: (FocusState) -> Unit = {},
    items: List<Pair<T, String>>,
    onSearch: (T?) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val query by remember { derivedStateOf { textFieldState.text.toString() } }

    val filteredItems: List<Pair<T, String>> by remember(items, query) {
        derivedStateOf {
            if (query.isBlank()) {
                items
            } else {
                val q = query.trim().lowercase()
                items.filter { (_, label) -> label.lowercase().contains(q) }
            }
        }
    }

    Box(modifier.then(Modifier.semantics { isTraversalGroup = true })) {
        SearchBar(
            modifier = Modifier
                .onFocusChanged(onFocusChanged)
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = textFieldState.text.toString(),
                    onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                    onSearch = {
                        val searchText = textFieldState.text.toString()
                        val selectedItem: Pair<T, String>? = filteredItems.find { it.second == searchText }
                        if (selectedItem != null) {
                            onSearch(selectedItem.first)
                            expanded = false
                        }
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { RmText(placeholder) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_search),
                            contentDescription = stringResource(Res.string.search_bar_search_icon_content_description),
                            tint = textColor
                        )
                    },
                    trailingIcon = {
                        if (expanded || textFieldState.text.isNotEmpty()) {
                            IconButton(
                                modifier = Modifier.size(32.dp),
                                onClick = {
                                    textFieldState.edit { replace(0, length, "") }
                                    onSearch(null)
                                    expanded = false
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
            },
            expanded = expanded,
            colors = SearchBarDefaults.colors(containerColor = backgroundColorForItems),
            onExpandedChange = { expanded = it },
        ) {
            LazyColumn {
                items(count = filteredItems.size) { index ->
                    val result: Pair<T, String> = filteredItems[index]
                    ListItem(
                        headlineContent = { RmText(result.second) },
                        colors = ListItemDefaults.colors(containerColor = backgroundColorForItems),
                        modifier = Modifier
                            .clickable {
                                textFieldState.edit { replace(0, length, result.second) }
                                onSearch(result.first)
                                expanded = false
                            }
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}
