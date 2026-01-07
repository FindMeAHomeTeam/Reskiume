package com.findmeahometeam.reskiume.ui.profile.checkAllAdvice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.Advice
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmDialog
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmListItem
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmSearchBar
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.CheckAdvice
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.secondaryGreen
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import com.findmeahometeam.reskiume.ui.profile.giveFeedback.GiveFeedback
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_advice_screen_no_advice_found
import reskiume.composeapp.generated.resources.check_all_advice_screen_option_advice_mail_body
import reskiume.composeapp.generated.resources.check_all_advice_screen_option_advice_mail_subject
import reskiume.composeapp.generated.resources.check_all_advice_screen_option_send_advice_button
import reskiume.composeapp.generated.resources.check_all_advice_screen_title
import reskiume.composeapp.generated.resources.give_feedback_no_email_app_dialog_message
import reskiume.composeapp.generated.resources.give_feedback_no_email_app_dialog_ok_button
import reskiume.composeapp.generated.resources.give_feedback_no_email_app_dialog_title
import reskiume.composeapp.generated.resources.ic_mail

@Composable
fun CheckAllAdviceScreen(
    onBackPressed: () -> Unit,
    onSeeAdvice: (CheckAdvice) -> Unit
) {
    val checkAllAdviceViewmodel: CheckAllAdviceViewmodel = koinViewModel<CheckAllAdviceViewmodel>()

    val adviceListState: UiState<List<Advice>> by checkAllAdviceViewmodel.adviceListState.collectAsState()

    RmScaffold(
        title = stringResource(Res.string.check_all_advice_screen_title),
        onBackPressed = onBackPressed,
        floatingActionButton = {
            DisplayExtendedFloatingActionButton(checkAllAdviceViewmodel)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RmSearchBar {
                checkAllAdviceViewmodel.searchAdvice(it)
            }
            Spacer(modifier = Modifier.height(8.dp))

            DisplaySingleChoiceSegmentedButtonRow(checkAllAdviceViewmodel)
            Spacer(modifier = Modifier.height(8.dp))

            RmResultState(adviceListState) { adviceList: List<Advice> ->

                AnimatedVisibility(
                    visible = adviceList.isEmpty(),
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        RmText(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(Res.string.check_all_advice_screen_no_advice_found),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                if (adviceList.isNotEmpty()) {
                    LazyColumn {
                        items(adviceList, key = { advice -> advice.hashCode() }) { advice ->

                            val title = stringResource(advice.title)
                            val description = stringResource(advice.description)
                            RmListItem(
                                modifier = Modifier.animateItem(),
                                title = title,
                                description = description,
                                listAvatarType = RmListAvatarType.Icon(
                                    backgroundColor = advice.image.backgroundColor,
                                    icon = advice.image.icon,
                                    iconColor = advice.image.iconColor
                                ),
                                onClick = {
                                    checkAllAdviceViewmodel.retrieveAdviceAuthor(advice.authorId) { author: User? ->

                                        onSeeAdvice(
                                            CheckAdvice(
                                                title = title,
                                                description = description,
                                                image = advice.image.name,
                                                authorUid = author?.uid,
                                                authorName = author?.username,
                                                authorImage = author?.image
                                            )
                                        )
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DisplaySingleChoiceSegmentedButtonRow(checkAllAdviceViewmodel: CheckAllAdviceViewmodel) {

    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    SingleChoiceSegmentedButtonRow {

        AdviceType.entries.forEachIndexed { index, adviceType ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = AdviceType.entries.size
                ),
                onClick = {
                    selectedIndex = index
                    checkAllAdviceViewmodel.updateAdviceList(adviceType)
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
                        text = stringResource(adviceType.stringResource),
                        color = Color.Black
                    )
                }
            )
        }
    }
}

@Composable
private fun DisplayExtendedFloatingActionButton(checkAllAdviceViewmodel: CheckAllAdviceViewmodel) {

    val giveFeedback: GiveFeedback = koinInject<GiveFeedback>()

    var displaySendAdviceButton by rememberSaveable { mutableStateOf(false) }
    var displayNoEmailAppError: Boolean by remember { mutableStateOf(false) }

    checkAllAdviceViewmodel.checkAuthState { isLoggedIn ->
        displaySendAdviceButton = isLoggedIn
    }
    if (displaySendAdviceButton) {

        val sendAdviceSubject =
            stringResource(Res.string.check_all_advice_screen_option_advice_mail_subject)
        val sendAdviceBody =
            stringResource(Res.string.check_all_advice_screen_option_advice_mail_body)
        ExtendedFloatingActionButton(
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(Res.drawable.ic_mail),
                    contentDescription = null,
                    tint = primaryGreen
                )
            },
            text = {
                RmText(
                    text = stringResource(Res.string.check_all_advice_screen_option_send_advice_button),
                    color = Color.Black
                )
            },
            containerColor = tertiaryGreen,
            onClick = {
                giveFeedback.sendEmail(
                    subject = sendAdviceSubject,
                    body = sendAdviceBody,
                    onError = {
                        displayNoEmailAppError = true
                    }
                )
            }
        )
        if (displayNoEmailAppError) {

            RmDialog(
                emoji = "✉️",
                title = stringResource(Res.string.give_feedback_no_email_app_dialog_title),
                message = stringResource(Res.string.give_feedback_no_email_app_dialog_message),
                allowMessage = stringResource(Res.string.give_feedback_no_email_app_dialog_ok_button),
                onClickAllow = { displayNoEmailAppError = false },
                onClickDeny = { displayNoEmailAppError = false }
            )
        }
    }
}
