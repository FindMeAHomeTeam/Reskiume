package com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmRescueEventListItem
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.UiState
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_my_rescue_events_screen_no_rescue_events
import reskiume.composeapp.generated.resources.check_all_my_rescue_events_screen_register_rescue_event
import reskiume.composeapp.generated.resources.check_all_my_rescue_events_screen_title

@Composable
fun CheckAllMyRescueEventsScreen(
    myUid: String,
    onBackPressed: () -> Unit,
    onModifyRescueEvent: (rescueEventId: String) -> Unit,
    onCreateRescueEvent: (creatorId: String) -> Unit
) {
    val checkAllMyRescueEventsViewmodel: CheckAllMyRescueEventsViewmodel =
        koinViewModel<CheckAllMyRescueEventsViewmodel>()

    val uiRescueEventListState: UiState<List<UiRescueEvent>> by checkAllMyRescueEventsViewmodel.fetchAllMyRescueEvents()
        .collectAsState(initial = UiState.Loading())

    RmScaffold(
        onBackPressed = onBackPressed,
        title = stringResource(Res.string.check_all_my_rescue_events_screen_title)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            RmResultState(uiRescueEventListState) { uiRescueEventList: List<UiRescueEvent> ->

                if (uiRescueEventList.isEmpty()) {
                    Spacer(modifier = Modifier.weight(1f))
                    RmText(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.check_all_my_rescue_events_screen_no_rescue_events),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.weight(1f))
                } else {

                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(
                            items = uiRescueEventList,
                            key = { it.hashCode() }
                        ) { uiRescueEvent ->
                            RmRescueEventListItem(
                                modifier = Modifier.animateItem(),
                                title = uiRescueEvent.rescueEvent.title,
                                imageUrl = uiRescueEvent.rescueEvent.imageUrl,
                                allNeedsToCover = uiRescueEvent.rescueEvent.allNeedsToCover,
                                allNonHumanAnimalsToRescue = uiRescueEvent.allUiNonHumanAnimalsToRescue,
                                distance = null,
                                city = uiRescueEvent.rescueEvent.city,
                                onClick = {
                                    onModifyRescueEvent(uiRescueEvent.rescueEvent.id)
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                RmButton(
                    text = stringResource(Res.string.check_all_my_rescue_events_screen_register_rescue_event),
                    onClick = {
                        onCreateRescueEvent(myUid)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
