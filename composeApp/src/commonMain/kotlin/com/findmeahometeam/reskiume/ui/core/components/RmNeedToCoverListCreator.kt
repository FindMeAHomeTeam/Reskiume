package com.findmeahometeam.reskiume.ui.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCover
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCoverListSaver
import com.findmeahometeam.reskiume.domain.model.rescueEvent.RescueNeed
import com.findmeahometeam.reskiume.domain.model.rescueEvent.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import org.jetbrains.compose.resources.stringResource
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.need_to_cover_list_creator_title
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun RmNeedToCoverListCreator(
    rescueEventId: String,
    allNeedsToCover: List<NeedToCover>,
    onAddNeedToCover: (List<NeedToCover>) -> Unit
) {
    var itemsAdded: List<NeedToCover> by rememberSaveable(stateSaver = NeedToCoverListSaver) {
        mutableStateOf(
            allNeedsToCover
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColorForItems, shape = RoundedCornerShape(15.dp))
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(15.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        RmText(
            text = stringResource(Res.string.need_to_cover_list_creator_title),
            fontSize = 16.sp
        )
        RescueNeed.entries.forEach { rescueNeed ->

            if (rescueNeed == RescueNeed.UNSELECTED) return@forEach

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColorForItems, shape = RoundedCornerShape(15.dp))
                    .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(15.dp)),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RmCheckbox(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(rescueNeed.toStringResource()),
                    isChecked = itemsAdded.any { it.rescueNeed == rescueNeed },
                    onChecked = { isChecked ->
                        itemsAdded =
                            if (isChecked) {
                                itemsAdded + NeedToCover(
                                    needToCoverId = Clock.System.now().epochSeconds,
                                    rescueNeed = rescueNeed,
                                    rescueEventId = rescueEventId
                                )
                            } else {
                                itemsAdded.filterNot { it.rescueNeed == rescueNeed }
                            }
                        onAddNeedToCover(itemsAdded)
                    }
                )
            }
        }
    }
}
