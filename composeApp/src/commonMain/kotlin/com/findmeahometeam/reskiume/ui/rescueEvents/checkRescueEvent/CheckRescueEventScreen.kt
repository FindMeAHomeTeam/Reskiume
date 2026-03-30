package com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.model.fosterHome.City
import com.findmeahometeam.reskiume.domain.model.fosterHome.Country
import com.findmeahometeam.reskiume.domain.model.fosterHome.toStringResource
import com.findmeahometeam.reskiume.domain.model.rescueEvent.NeedToCover
import com.findmeahometeam.reskiume.domain.model.rescueEvent.toStringResource
import com.findmeahometeam.reskiume.domain.model.toEmoji
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmImage
import com.findmeahometeam.reskiume.ui.core.components.RmReport
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmShareService
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.RmTextLink
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.RESCUE_EVENT_DEEP_LINK
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.textColor
import com.findmeahometeam.reskiume.ui.profile.checkAllMyRescueEvents.UiRescueEvent
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_foster_home_screen_share_rescue_event_title
import reskiume.composeapp.generated.resources.check_rescue_event_screen_creator_avatar_content_description
import reskiume.composeapp.generated.resources.check_rescue_event_screen_join_the_rescue_event_label
import reskiume.composeapp.generated.resources.check_rescue_event_screen_no_account_button
import reskiume.composeapp.generated.resources.check_rescue_event_screen_no_account_label
import reskiume.composeapp.generated.resources.check_rescue_event_screen_non_human_animals_to_rescue
import reskiume.composeapp.generated.resources.check_rescue_event_screen_rescue_event_avatar_content_description
import reskiume.composeapp.generated.resources.check_rescue_event_screen_rescue_event_description_label
import reskiume.composeapp.generated.resources.check_rescue_event_screen_rescue_event_needs_to_cover_label
import reskiume.composeapp.generated.resources.check_rescue_event_screen_rescue_event_non_human_animals_to_rescue_label
import reskiume.composeapp.generated.resources.check_rescue_event_screen_share_content_description
import reskiume.composeapp.generated.resources.check_rescue_event_screen_start_chat_button
import reskiume.composeapp.generated.resources.check_rescue_event_screen_title
import reskiume.composeapp.generated.resources.general_error_invalid_deep_link
import reskiume.composeapp.generated.resources.ic_share
import reskiume.composeapp.generated.resources.rescue_event
import reskiume.composeapp.generated.resources.reskiume

@Composable
fun CheckRescueEventScreen(
    onContactRescueEvent: (rescueEventId: String, nonHumanAnimalIds: List<String>) -> Unit,
    onCreateAccount: () -> Unit,
    onBackPressed: () -> Unit
) {
    val checkRescueEventViewmodel: CheckRescueEventViewmodel =
        koinViewModel<CheckRescueEventViewmodel>()

    val uiRescueEventState: UiState<UiRescueEvent> by checkRescueEventViewmodel.rescueEventFlow.collectAsStateWithLifecycle(
        initialValue = UiState.Loading()
    )

    var isShareButtonClicked: Boolean by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    RmScaffold(
        title =
            if (uiRescueEventState is UiState.Success) {
                stringResource(
                    Res.string.check_rescue_event_screen_title,
                    (uiRescueEventState as UiState.Success<UiRescueEvent>).data.rescueEvent.title,
                    stringResource(
                        City
                            .valueOf((uiRescueEventState as UiState.Success<UiRescueEvent>).data.rescueEvent.city)
                            .toStringResource()
                    ).substring(5),
                    stringResource(
                        Country
                            .valueOf((uiRescueEventState as UiState.Success<UiRescueEvent>).data.rescueEvent.country)
                            .toStringResource()
                    ).substring(5)
                )
            } else {
                ""
            },
        topAppBarActions = {
            if (uiRescueEventState is UiState.Success) {
                IconButton(
                    modifier = Modifier.padding(end = 16.dp).size(32.dp),
                    onClick = {
                        isShareButtonClicked = true
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_share),
                        contentDescription = stringResource(Res.string.check_rescue_event_screen_share_content_description),
                        tint = textColor,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        },
        onBackPressed = onBackPressed,
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RmResultState(
                uiState = uiRescueEventState,
                customErrorMessage = stringResource(
                    Res.string.general_error_invalid_deep_link,
                    stringResource(Res.string.rescue_event)
                )
            ) { uiRescueEvent: UiRescueEvent ->

                if (isShareButtonClicked) {
                    DisplayShareService(
                        rescueEventTitle = uiRescueEvent.rescueEvent.title,
                        allNonHumanAnimalsToRescue = uiRescueEvent.allUiNonHumanAnimalsToRescue,
                        rescueEventCreatorId = uiRescueEvent.rescueEvent.creatorId,
                        rescueEventId = uiRescueEvent.rescueEvent.id
                    )
                    isShareButtonClicked = false
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmImage(
                    modifier = Modifier.height(300.dp).clip(RoundedCornerShape(15.dp)),
                    imagePath = uiRescueEvent.rescueEvent.imageUrl,
                    contentDescription =
                        stringResource(
                            Res.string.check_rescue_event_screen_rescue_event_avatar_content_description,
                            uiRescueEvent.rescueEvent.title
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DisplayCreator(uiRescueEvent.creator!!)

                    RmReport(
                        stringResource(Res.string.rescue_event),
                        uiRescueEvent.rescueEvent.id,
                        uiRescueEvent.rescueEvent.title
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = stringResource(Res.string.check_rescue_event_screen_rescue_event_description_label),
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = uiRescueEvent.rescueEvent.description
                )

                Spacer(modifier = Modifier.height(8.dp))
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = stringResource(Res.string.check_rescue_event_screen_rescue_event_needs_to_cover_label),
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                NeedToCoverList(uiRescueEvent.rescueEvent.allNeedsToCover)

                Spacer(modifier = Modifier.height(8.dp))
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = stringResource(Res.string.check_rescue_event_screen_rescue_event_non_human_animals_to_rescue_label),
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                NonHumanAnimalToRescueList(
                    uiRescueEvent.allUiNonHumanAnimalsToRescue
                )

                if (checkRescueEventViewmodel.canIStartTheChat()) {

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth().alpha(0.1f),
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (checkRescueEventViewmodel.isLoggedIn()) {
                        RmText(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            text = stringResource(
                                Res.string.check_rescue_event_screen_join_the_rescue_event_label,
                                uiRescueEvent.rescueEvent.title
                            ),
                            textAlign = TextAlign.Start,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        RmButton(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(Res.string.check_rescue_event_screen_start_chat_button),
                        ) {
                            onContactRescueEvent(
                                uiRescueEvent.rescueEvent.id,
                                uiRescueEvent.allUiNonHumanAnimalsToRescue.map { it.id }
                            )
                        }
                    } else {
                        RmTextLink(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            text = stringResource(
                                Res.string.check_rescue_event_screen_no_account_label,
                                uiRescueEvent.creator!!.username
                            ),
                            textToLink = stringResource(Res.string.check_rescue_event_screen_no_account_button),
                            textAlign = TextAlign.Start,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            onClick = onCreateAccount
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun DisplayShareService(
    rescueEventTitle: String,
    allNonHumanAnimalsToRescue: List<NonHumanAnimal>,
    rescueEventCreatorId: String,
    rescueEventId: String
) {
    val nonHumanAnimalsToRescueText = if(allNonHumanAnimalsToRescue.size == 1) {
        "${allNonHumanAnimalsToRescue[0].nonHumanAnimalType.toEmoji()} ${allNonHumanAnimalsToRescue[0].name}"
    } else {
        stringResource(
            Res.string.check_rescue_event_screen_non_human_animals_to_rescue,
            allNonHumanAnimalsToRescue
                .subList(0, allNonHumanAnimalsToRescue.size - 1)
                .joinToString(", ") { "${it.nonHumanAnimalType.toEmoji()} ${it.name}" },
            "${allNonHumanAnimalsToRescue.last().nonHumanAnimalType.toEmoji()} ${allNonHumanAnimalsToRescue.last().name}"
        )
    }
    val rescueEventDeepLink = "$RESCUE_EVENT_DEEP_LINK/$rescueEventCreatorId/$rescueEventId"

    RmShareService(
        Res.string.check_foster_home_screen_share_rescue_event_title,
        rescueEventTitle,
        nonHumanAnimalsToRescueText,
        rescueEventDeepLink
    )
}

@Composable
fun DisplayCreator(creator: User) {

    Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (creator.image.isBlank()) {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(Res.drawable.reskiume),
                contentDescription =
                    stringResource(
                        Res.string.check_rescue_event_screen_creator_avatar_content_description,
                        creator.username
                    ),
                tint = primaryGreen
            )
        } else {
            RmImage(
                modifier = Modifier.size(40.dp).clip(CircleShape),
                imagePath = creator.image,
                contentDescription = stringResource(
                    Res.string.check_rescue_event_screen_creator_avatar_content_description,
                    creator.username
                )
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        RmText(
            text = creator.username,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun NeedToCoverList(allNeedsToCover: List<NeedToCover>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(backgroundColorForItems, shape = RoundedCornerShape(15.dp))
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(15.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        allNeedsToCover.forEachIndexed { index, needToCover ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RmText(
                    text = stringResource(needToCover.rescueNeed.toStringResource()),
                    fontSize = 16.sp
                )
            }
            if (index < allNeedsToCover.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun NonHumanAnimalToRescueList(allNonHumanAnimalsToRescue: List<NonHumanAnimal>) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(backgroundColorForItems, shape = RoundedCornerShape(15.dp))
            .border(BorderStroke(1.dp, Color.Black), shape = RoundedCornerShape(15.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        allNonHumanAnimalsToRescue.forEachIndexed { index, nonHumanAnimalToRescue ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RmText(
                    text = nonHumanAnimalToRescue.nonHumanAnimalType.toEmoji()
                            + " " + nonHumanAnimalToRescue.name
                            + " · " + nonHumanAnimalToRescue.gender.toEmoji()
                            + " " + stringResource(nonHumanAnimalToRescue.gender.toStringResource()),
                    fontSize = 16.sp
                )
            }
            if (index < allNonHumanAnimalsToRescue.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
