package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.findmeahometeam.reskiume.ui.core.secondaryGreen
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen

@Composable
fun <T : Enum<T>> RmDisplaySingleChoiceSegmentedButtonRow(
    items: List<Pair<T, String>>,
    modifier: Modifier = Modifier,
    onClick: (T) -> Unit
) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    SingleChoiceSegmentedButtonRow {

        items.forEachIndexed { index, element: Pair<T, String> ->
            SegmentedButton(
                modifier = modifier,
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = items.size
                ),
                onClick = {
                    selectedIndex = index
                    onClick(element.first)
                },
                selected = index == selectedIndex,
                colors = SegmentedButtonDefaults.colors().copy(
                    activeContainerColor = secondaryGreen,
                    inactiveContainerColor = tertiaryGreen,
                    activeBorderColor = Color.Black,
                    inactiveBorderColor = Color.Black
                ),
                label = {
                    RmText(
                        text = element.second,
                        color = Color.Black
                    )
                }
            )
        }
    }
}
