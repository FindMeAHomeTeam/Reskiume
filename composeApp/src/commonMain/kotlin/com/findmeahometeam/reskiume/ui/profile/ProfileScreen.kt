package com.findmeahometeam.reskiume.ui.profile

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmDialog
import com.findmeahometeam.reskiume.ui.core.components.RmHeader
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmListButtonItem
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.primaryBlue
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.primaryRed
import com.findmeahometeam.reskiume.ui.core.secondaryBlue
import com.findmeahometeam.reskiume.ui.core.secondaryRed
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import com.findmeahometeam.reskiume.ui.profile.giveFeedback.GiveFeedback
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.give_feedback_dialog_message
import reskiume.composeapp.generated.resources.give_feedback_dialog_ok_button
import reskiume.composeapp.generated.resources.give_feedback_dialog_title
import reskiume.composeapp.generated.resources.give_feedback_subject
import reskiume.composeapp.generated.resources.ic_advice
import reskiume.composeapp.generated.resources.ic_delete
import reskiume.composeapp.generated.resources.ic_feedback
import reskiume.composeapp.generated.resources.ic_foster_homes
import reskiume.composeapp.generated.resources.ic_paw
import reskiume.composeapp.generated.resources.ic_rating
import reskiume.composeapp.generated.resources.ic_rescue_events
import reskiume.composeapp.generated.resources.ic_user
import reskiume.composeapp.generated.resources.profile_screen_delete_account_description
import reskiume.composeapp.generated.resources.profile_screen_delete_account_title
import reskiume.composeapp.generated.resources.profile_screen_feedback_description
import reskiume.composeapp.generated.resources.profile_screen_feedback_title
import reskiume.composeapp.generated.resources.profile_screen_get_advice_description
import reskiume.composeapp.generated.resources.profile_screen_get_advice_title
import reskiume.composeapp.generated.resources.profile_screen_modify_account_description
import reskiume.composeapp.generated.resources.profile_screen_modify_account_title
import reskiume.composeapp.generated.resources.profile_screen_my_account_section
import reskiume.composeapp.generated.resources.profile_screen_my_activism_section
import reskiume.composeapp.generated.resources.profile_screen_my_foster_homes_description
import reskiume.composeapp.generated.resources.profile_screen_my_foster_homes_title
import reskiume.composeapp.generated.resources.profile_screen_my_rescue_events_description
import reskiume.composeapp.generated.resources.profile_screen_my_rescue_events_title
import reskiume.composeapp.generated.resources.profile_screen_non_human_animals_description
import reskiume.composeapp.generated.resources.profile_screen_non_human_animals_title
import reskiume.composeapp.generated.resources.profile_screen_personal_create_account_description
import reskiume.composeapp.generated.resources.profile_screen_personal_create_account_title
import reskiume.composeapp.generated.resources.profile_screen_reviews_description
import reskiume.composeapp.generated.resources.profile_screen_reviews_title
import reskiume.composeapp.generated.resources.profile_screen_settings_section

@Composable
fun ProfileScreen(
    navigateToCreateAccountScreen: () -> Unit,
    navigateToModifyAccountScreen: () -> Unit,
    navigateToCheckReviewsScreen: (uid: String) -> Unit,
    navigateToCheckNonHumanAnimalsScreen: (uid: String) -> Unit,
    navigateToDeleteAccountScreen: () -> Unit
) {
    val profileViewmodel: ProfileViewmodel = koinViewModel<ProfileViewmodel>()
    val profileUiState: ProfileViewmodel.ProfileUiState by profileViewmodel.state.collectAsState(
        initial = ProfileViewmodel.ProfileUiState.Idle
    )
    val giveFeedback: GiveFeedback = koinInject<GiveFeedback>()
    var user: User? by remember { mutableStateOf(null) }
    var displayNoEmailAppError: Boolean by remember { mutableStateOf(false) }

    val scrollState: ScrollState = rememberScrollState()

    user = when (profileUiState) {
        is ProfileViewmodel.ProfileUiState.Idle -> {
            null
        }

        is ProfileViewmodel.ProfileUiState.Error -> {
            profileViewmodel.logError(
                "ProfileScreen",
                (profileUiState as ProfileViewmodel.ProfileUiState.Error).message
            )
            null
        }

        is ProfileViewmodel.ProfileUiState.Success -> {
            (profileUiState as ProfileViewmodel.ProfileUiState.Success).user
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        RmHeader(user)

        Spacer(Modifier.height(32.dp))
        RmText(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            text = stringResource(Res.string.profile_screen_my_account_section),
            textAlign = TextAlign.Start,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        // create account screen
        if (user == null) {
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_personal_create_account_title),
                description = stringResource(Res.string.profile_screen_personal_create_account_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_user,
                    iconColor = primaryGreen
                ),
                onClick = {
                    navigateToCreateAccountScreen()
                }
            )
        }
        if (user != null) {

            // modify account screen
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_modify_account_title),
                description = stringResource(Res.string.profile_screen_modify_account_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_user,
                    iconColor = primaryGreen
                ),
                onClick = {
                    navigateToModifyAccountScreen()
                }
            )

            // Check review screen
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_reviews_title),
                description = stringResource(Res.string.profile_screen_reviews_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_rating,
                    iconColor = primaryGreen
                ),
                onClick = {
                    navigateToCheckReviewsScreen(user?.uid ?: "")
                }
            )
        }

        Spacer(Modifier.height(32.dp))
        RmText(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            text = stringResource(Res.string.profile_screen_my_activism_section),
            textAlign = TextAlign.Start,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (user != null) {

            // Non-human animals screen
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_non_human_animals_title),
                description = stringResource(Res.string.profile_screen_non_human_animals_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_paw,
                    iconColor = primaryGreen
                ),
                onClick = {
                    navigateToCheckNonHumanAnimalsScreen(user!!.uid)
                }
            )

            // Foster homes screen
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_my_foster_homes_title),
                description = stringResource(Res.string.profile_screen_my_foster_homes_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_foster_homes,
                    iconColor = primaryGreen
                ),
                onClick = {
                    // TODO
                }
            )

            // Rescue events screen
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_my_rescue_events_title),
                description = stringResource(Res.string.profile_screen_my_rescue_events_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = tertiaryGreen,
                    icon = Res.drawable.ic_rescue_events,
                    iconColor = primaryGreen
                ),
                onClick = {
                    // TODO
                }
            )
        }

        // Advice screen
        RmListButtonItem(
            title = stringResource(Res.string.profile_screen_get_advice_title),
            description = stringResource(Res.string.profile_screen_get_advice_description),
            containerColor = backgroundColor,
            listAvatarType = RmListAvatarType.Icon(
                backgroundColor = tertiaryGreen,
                icon = Res.drawable.ic_advice,
                iconColor = primaryGreen
            ),
            onClick = {
                // TODO
            }
        )
        Spacer(Modifier.height(32.dp))
        RmText(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            text = stringResource(Res.string.profile_screen_settings_section),
            textAlign = TextAlign.Start,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Feedback screen
        val feedbackSubject = stringResource(Res.string.give_feedback_subject)
        RmListButtonItem(
            title = stringResource(Res.string.profile_screen_feedback_title),
            description = stringResource(Res.string.profile_screen_feedback_description),
            containerColor = backgroundColor,
            listAvatarType = RmListAvatarType.Icon(
                backgroundColor = secondaryBlue,
                icon = Res.drawable.ic_feedback,
                iconColor = primaryBlue
            ),
            onClick = {
                giveFeedback.sendEmail(
                    subject = feedbackSubject,
                    onError = {
                        displayNoEmailAppError = true
                    }
                )
            }
        )
        if (displayNoEmailAppError) {

            RmDialog(
                emoji = "✉️",
                title = stringResource(Res.string.give_feedback_dialog_title),
                message = stringResource(Res.string.give_feedback_dialog_message),
                allowMessage = stringResource(Res.string.give_feedback_dialog_ok_button),
                onClickAllow = { displayNoEmailAppError = false },
                onClickDeny = { displayNoEmailAppError = false }
            )
        }

        // Delete account screen
        if (user != null) {
            RmListButtonItem(
                title = stringResource(Res.string.profile_screen_delete_account_title),
                description = stringResource(Res.string.profile_screen_delete_account_description),
                containerColor = backgroundColor,
                listAvatarType = RmListAvatarType.Icon(
                    backgroundColor = secondaryRed,
                    icon = Res.drawable.ic_delete,
                    iconColor = primaryRed
                ),
                onClick = {
                    navigateToDeleteAccountScreen()
                }
            )
        }
        Spacer(modifier = Modifier.height(15.dp))
    }
}
