package com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.findmeahometeam.reskiume.domain.model.Gender
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimal
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalType
import com.findmeahometeam.reskiume.domain.model.User
import com.findmeahometeam.reskiume.domain.model.fosterHome.AcceptedNonHumanAnimalForFosterHome
import com.findmeahometeam.reskiume.domain.model.toEmoji
import com.findmeahometeam.reskiume.domain.model.toStringResource
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmDialog
import com.findmeahometeam.reskiume.ui.core.components.RmDropDownMenu
import com.findmeahometeam.reskiume.ui.core.components.RmImage
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmListReviewItem
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.RmTextLink
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.navigation.FOSTER_HOME_DEEP_LINK
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.primaryRed
import com.findmeahometeam.reskiume.ui.core.textColor
import com.findmeahometeam.reskiume.ui.fosterHomes.checkAllFosterHomes.UiFosterHome
import com.findmeahometeam.reskiume.ui.fosterHomes.shareService.ShareService
import com.findmeahometeam.reskiume.ui.profile.checkReviews.UiReview
import com.findmeahometeam.reskiume.ui.profile.giveFeedback.GiveFeedback
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_foster_home_screen_accepted_both_genders_non_human_animal
import reskiume.composeapp.generated.resources.check_foster_home_screen_accepted_non_human_animals
import reskiume.composeapp.generated.resources.check_foster_home_screen_foster_home_accepted_non_human_animals_label
import reskiume.composeapp.generated.resources.check_foster_home_screen_foster_home_avatar_content_description
import reskiume.composeapp.generated.resources.check_foster_home_screen_foster_home_conditions_label
import reskiume.composeapp.generated.resources.check_foster_home_screen_foster_home_description_label
import reskiume.composeapp.generated.resources.check_foster_home_screen_foster_home_resident_non_human_animals_label
import reskiume.composeapp.generated.resources.check_foster_home_screen_no_account_button
import reskiume.composeapp.generated.resources.check_foster_home_screen_no_account_label
import reskiume.composeapp.generated.resources.check_foster_home_screen_non_human_animal_label
import reskiume.composeapp.generated.resources.check_foster_home_screen_owner_avatar_content_description
import reskiume.composeapp.generated.resources.check_foster_home_screen_owner_reviews_label
import reskiume.composeapp.generated.resources.check_foster_home_screen_register_non_human_animal_button
import reskiume.composeapp.generated.resources.check_foster_home_screen_register_non_human_animal_to_talk_to_owner_label
import reskiume.composeapp.generated.resources.check_foster_home_screen_report_foster_home
import reskiume.composeapp.generated.resources.check_foster_home_screen_report_foster_home_body
import reskiume.composeapp.generated.resources.check_foster_home_screen_report_foster_home_subject
import reskiume.composeapp.generated.resources.check_foster_home_screen_share_content_description
import reskiume.composeapp.generated.resources.check_foster_home_screen_share_foster_home_title
import reskiume.composeapp.generated.resources.check_foster_home_screen_share_message
import reskiume.composeapp.generated.resources.check_foster_home_screen_share_ok_button
import reskiume.composeapp.generated.resources.check_foster_home_screen_share_title
import reskiume.composeapp.generated.resources.check_foster_home_screen_start_chat_button
import reskiume.composeapp.generated.resources.check_foster_home_screen_talk_to_owner_label
import reskiume.composeapp.generated.resources.check_foster_home_screen_title
import reskiume.composeapp.generated.resources.check_foster_home_screen_unselected_non_human_animal_label
import reskiume.composeapp.generated.resources.check_reviews_screen_user_deleted
import reskiume.composeapp.generated.resources.dialog_no_email_app_dialog_message
import reskiume.composeapp.generated.resources.dialog_no_email_app_dialog_ok_button
import reskiume.composeapp.generated.resources.dialog_no_email_app_dialog_title
import reskiume.composeapp.generated.resources.ic_share
import reskiume.composeapp.generated.resources.reskiume

@Composable
fun CheckFosterHomeScreen(
    onContactFosterHome: (fosterHomeId: String, nonHumanAnimalId: String) -> Unit,
    onReviewClick: (uid: String) -> Unit,
    onCreateAccount: () -> Unit,
    onCreateNonHumanAnimal: () -> Unit,
    onBackPressed: () -> Unit
) {
    val checkFosterHomeViewmodel: CheckFosterHomeViewmodel =
        koinViewModel<CheckFosterHomeViewmodel>()

    val uiFosterHomeState: UiState<UiFosterHome> by checkFosterHomeViewmodel.fosterHomeFlow.collectAsState(
        initial = UiState.Loading()
    )

    val allUserUiReviewsState: UiState<List<UiReview>> by checkFosterHomeViewmodel.reviewListFlowState.collectAsState(
        initial = UiState.Loading()
    )

    val allAvailableNonHumanAnimals: List<NonHumanAnimal> by checkFosterHomeViewmodel.allAvailableNonHumanAnimalsLookingForAdoptionFlow.collectAsState(
        initial = emptyList()
    )

    var isShareButtonClicked: Boolean by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    RmScaffold(
        title =
            if (uiFosterHomeState is UiState.Success) {
                stringResource(
                    Res.string.check_foster_home_screen_title,
                    (uiFosterHomeState as UiState.Success<UiFosterHome>).data.fosterHome.title,
                    stringResource(
                        City
                            .valueOf((uiFosterHomeState as UiState.Success<UiFosterHome>).data.fosterHome.city)
                            .toStringResource()
                    ).substring(5),
                    stringResource(
                        Country
                            .valueOf((uiFosterHomeState as UiState.Success<UiFosterHome>).data.fosterHome.country)
                            .toStringResource()
                    ).substring(5)
                )
            } else {
                ""
            },
        topAppBarActions = {
            if (uiFosterHomeState is UiState.Success) {
                IconButton(
                    modifier = Modifier.padding(end = 16.dp).size(32.dp),
                    onClick = {
                        isShareButtonClicked = true
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_share),
                        contentDescription = stringResource(Res.string.check_foster_home_screen_share_content_description),
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

            RmResultState(uiFosterHomeState) { uiFosterHome: UiFosterHome ->

                if (isShareButtonClicked) {
                    DisplayShareService(
                        allAcceptedNonHumanAnimals = uiFosterHome.fosterHome.allAcceptedNonHumanAnimals,
                        fosterHomeTitle = uiFosterHome.fosterHome.title,
                        fosterHomeOwnerId = uiFosterHome.fosterHome.ownerId,
                        fosterHomeId = uiFosterHome.fosterHome.id
                    )
                    isShareButtonClicked = false
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmImage(
                    modifier = Modifier.height(300.dp).clip(RoundedCornerShape(15.dp)),
                    imagePath = uiFosterHome.fosterHome.imageUrl,
                    contentDescription =
                        stringResource(
                            Res.string.check_foster_home_screen_foster_home_avatar_content_description,
                            uiFosterHome.fosterHome.title
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DisplayOwner(uiFosterHome.owner!!)

                    DisplayReportFosterHome(
                        uiFosterHome.fosterHome.id,
                        uiFosterHome.fosterHome.title
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = stringResource(Res.string.check_foster_home_screen_foster_home_description_label),
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = uiFosterHome.fosterHome.description
                )

                Spacer(modifier = Modifier.height(8.dp))
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = stringResource(Res.string.check_foster_home_screen_foster_home_conditions_label),
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = uiFosterHome.fosterHome.conditions
                )

                Spacer(modifier = Modifier.height(8.dp))
                RmText(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = stringResource(Res.string.check_foster_home_screen_foster_home_accepted_non_human_animals_label),
                    textAlign = TextAlign.Start,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                AcceptedNonHumanAnimalList(
                    uiFosterHome.fosterHome.allAcceptedNonHumanAnimals
                )

                if (uiFosterHome.allResidentUiNonHumanAnimals.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    RmText(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        text = stringResource(Res.string.check_foster_home_screen_foster_home_resident_non_human_animals_label),
                        textAlign = TextAlign.Start,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    ResidentNonHumanAnimalList(
                        uiFosterHome.allResidentUiNonHumanAnimals
                    )
                }

                if (allUserUiReviewsState is UiState.Success && (allUserUiReviewsState as UiState.Success<List<UiReview>>).data.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    RmText(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        text = stringResource(Res.string.check_foster_home_screen_owner_reviews_label),
                        textAlign = TextAlign.Start,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    DisplayReviews(allUserUiReviewsState, onReviewClick)
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.fillMaxWidth().alpha(0.1f), color = textColor)

                Spacer(modifier = Modifier.height(8.dp))

                if (checkFosterHomeViewmodel.isLoggedIn()) {
                    if (allAvailableNonHumanAnimals.isEmpty()) {
                        RmTextLink(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            text = stringResource(
                                Res.string.check_foster_home_screen_register_non_human_animal_to_talk_to_owner_label,
                                uiFosterHome.owner!!.username
                            ),
                            textToLink = stringResource(Res.string.check_foster_home_screen_register_non_human_animal_button),
                            textAlign = TextAlign.Start,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            onClick = onCreateNonHumanAnimal
                        )
                    } else {
                        RmText(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            text = stringResource(
                                Res.string.check_foster_home_screen_talk_to_owner_label,
                                uiFosterHome.owner!!.username
                            ),
                            textAlign = TextAlign.Start,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        DisplayChatToOwner(
                            allAvailableNonHumanAnimals = allAvailableNonHumanAnimals,
                            fosterHomeId = uiFosterHome.fosterHome.id,
                            onContactFosterHome = onContactFosterHome
                        )
                    }
                } else {
                    RmTextLink(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        text = stringResource(
                            Res.string.check_foster_home_screen_no_account_label,
                            uiFosterHome.owner!!.username
                        ),
                        textToLink = stringResource(Res.string.check_foster_home_screen_no_account_button),
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

@Composable
fun DisplayShareService(
    allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalForFosterHome>,
    fosterHomeOwnerId: String,
    fosterHomeId: String
) {
    val shareService = koinInject<ShareService>()
    var displayNoSharingAppError: Boolean by remember { mutableStateOf(false) }

    val nonHumanAnimalHashMap = HashMap<NonHumanAnimalType, Set<Gender>>()

    allAcceptedNonHumanAnimals.forEach { accepted ->

        nonHumanAnimalHashMap[accepted.acceptedNonHumanAnimalType] =
            nonHumanAnimalHashMap[accepted.acceptedNonHumanAnimalType]?.plus(accepted.acceptedNonHumanAnimalGender)
                ?: setOf(accepted.acceptedNonHumanAnimalGender)
    }
    val allAcceptedNonHumanAnimalTextList =
        nonHumanAnimalHashMap.map { (nonHumanAnimalType, genders) ->

            val nonHumanAnimalText: String =
                nonHumanAnimalType.toEmoji() + " " + stringResource(nonHumanAnimalType.toStringResource()).lowercase()

            val genderText = if (genders.size == 1) {
                stringResource(genders.first().toStringResource()).lowercase()
            } else {
                stringResource(Res.string.check_foster_home_screen_accepted_both_genders_non_human_animal)
            }
            "$nonHumanAnimalText ($genderText)"
        }

    val acceptedNonHumanAnimalsText = stringResource(
        Res.string.check_foster_home_screen_accepted_non_human_animals,
        allAcceptedNonHumanAnimalTextList.toList()
            .subList(0, allAcceptedNonHumanAnimalTextList.size - 1)
            .joinToString(", "),
        allAcceptedNonHumanAnimalTextList.last()
    )
    val fosterHomeDeepLink = "$FOSTER_HOME_DEEP_LINK/$fosterHomeOwnerId/$fosterHomeId"

    shareService.shareContent(
        text = stringResource(
            Res.string.check_foster_home_screen_share_foster_home_title,
            acceptedNonHumanAnimalsText,
            fosterHomeDeepLink
        ),
        onError = {
            displayNoSharingAppError = true
        }
    )
    if (displayNoSharingAppError) {

        RmDialog(
            emoji = "📲",
            title = stringResource(Res.string.check_foster_home_screen_share_title),
            message = stringResource(Res.string.check_foster_home_screen_share_message),
            allowMessage = stringResource(Res.string.check_foster_home_screen_share_ok_button),
            onClickAllow = { displayNoSharingAppError = false },
            onClickDeny = { displayNoSharingAppError = false }
        )
    }
}

@Composable
fun DisplayOwner(owner: User) {

    Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (owner.image.isBlank()) {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(Res.drawable.reskiume),
                contentDescription =
                    stringResource(
                        Res.string.check_foster_home_screen_owner_avatar_content_description,
                        owner.username
                    ),
                tint = primaryGreen
            )
        } else {
            RmImage(
                modifier = Modifier.size(40.dp).clip(CircleShape),
                imagePath = owner.image,
                contentDescription = stringResource(
                    Res.string.check_foster_home_screen_owner_avatar_content_description,
                    owner.username
                )
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        RmText(
            text = owner.username,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DisplayReportFosterHome(
    fosterHomeId: String,
    title: String
) {
    val giveFeedback: GiveFeedback = koinInject<GiveFeedback>()
    var displayNoEmailAppError: Boolean by remember { mutableStateOf(false) }

    val sendReportSubject =
        stringResource(
            Res.string.check_foster_home_screen_report_foster_home_subject,
            title,
            fosterHomeId
        )
    val sendReportBody =
        stringResource(
            Res.string.check_foster_home_screen_report_foster_home_body,
            title,
            fosterHomeId
        )
    RmTextLink(
        modifier = Modifier.padding(10.dp),
        text = stringResource(Res.string.check_foster_home_screen_report_foster_home),
        textToLink = stringResource(Res.string.check_foster_home_screen_report_foster_home),
        linkColor = primaryRed,
        onClick = {
            giveFeedback.sendEmail(
                subject = sendReportSubject,
                body = sendReportBody,
                onError = {
                    displayNoEmailAppError = true
                }
            )
        }
    )
    if (displayNoEmailAppError) {
        RmDialog(
            emoji = "✉️",
            title = stringResource(Res.string.dialog_no_email_app_dialog_title),
            message = stringResource(Res.string.dialog_no_email_app_dialog_message),
            allowMessage = stringResource(Res.string.dialog_no_email_app_dialog_ok_button),
            onClickAllow = { displayNoEmailAppError = false },
            onClickDeny = { displayNoEmailAppError = false }
        )
    }
}

@Composable
private fun AcceptedNonHumanAnimalList(allAcceptedNonHumanAnimals: List<AcceptedNonHumanAnimalForFosterHome>) {

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
        allAcceptedNonHumanAnimals.forEachIndexed { index, acceptedNonHumanAnimalForFosterHome ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RmText(
                    text = acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalType.toEmoji()
                            + " " + stringResource(acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalType.toStringResource())
                            + " · " + acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalGender.toEmoji()
                            + " " + stringResource(acceptedNonHumanAnimalForFosterHome.acceptedNonHumanAnimalGender.toStringResource()),
                    fontSize = 16.sp
                )
            }
            if (index < allAcceptedNonHumanAnimals.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ResidentNonHumanAnimalList(allResidentNonHumanAnimals: List<NonHumanAnimal>) {

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
        allResidentNonHumanAnimals.forEachIndexed { index, residentNonHumanAnimalForFosterHome ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RmText(
                    text = residentNonHumanAnimalForFosterHome.nonHumanAnimalType.toEmoji()
                            + " " + residentNonHumanAnimalForFosterHome.name
                            + " · " + residentNonHumanAnimalForFosterHome.gender.toEmoji()
                            + " " + stringResource(residentNonHumanAnimalForFosterHome.gender.toStringResource()),
                    fontSize = 16.sp
                )
            }
            if (index < allResidentNonHumanAnimals.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DisplayReviews(
    allUserUiReviewsState: UiState<List<UiReview>>,
    onReviewClick: (uid: String) -> Unit
) {
    RmResultState(allUserUiReviewsState) { allUserUiReviews ->

        allUserUiReviews.forEach { uiReview ->
            RmListReviewItem(
                author = uiReview.authorName.ifBlank { stringResource(Res.string.check_reviews_screen_user_deleted) },
                description = uiReview.description,
                listAvatarType = RmListAvatarType.Image(resource = uiReview.authorUri),
                rating = uiReview.rating,
                date = uiReview.date,
                onClick = {
                    if (uiReview.authorUid.isNotBlank()) {
                        onReviewClick(uiReview.authorUid)
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun DisplayChatToOwner(
    allAvailableNonHumanAnimals: List<NonHumanAnimal>,
    fosterHomeId: String,
    onContactFosterHome: (fosterHomeId: String, nonHumanAnimalId: String) -> Unit
) {
    var selectedNonHumanAnimalIdToFoster: String by rememberSaveable { mutableStateOf("") }

    RmDropDownMenu(
        modifier = Modifier.fillMaxWidth(),
        dropDownLabel = stringResource(Res.string.check_foster_home_screen_non_human_animal_label),
        defaultElementText = if (selectedNonHumanAnimalIdToFoster.isEmpty()) {
            stringResource(Res.string.check_foster_home_screen_unselected_non_human_animal_label)
        } else {
            val selectedNonHumanAnimalToFoster: NonHumanAnimal =
                allAvailableNonHumanAnimals.first { it.id == selectedNonHumanAnimalIdToFoster }
            selectedNonHumanAnimalToFoster.nonHumanAnimalType.toEmoji() + " " + selectedNonHumanAnimalToFoster.name
        },
        items = allAvailableNonHumanAnimals.map {
            Pair(it, it.nonHumanAnimalType.toEmoji() + " " + it.name)
        },
        onClick = {
            selectedNonHumanAnimalIdToFoster = it.id
        },
    )
    Spacer(modifier = Modifier.height(8.dp))
    RmButton(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(Res.string.check_foster_home_screen_start_chat_button),
        enabled = selectedNonHumanAnimalIdToFoster.isNotEmpty()
    ) {
        onContactFosterHome(fosterHomeId, selectedNonHumanAnimalIdToFoster)
    }
}
