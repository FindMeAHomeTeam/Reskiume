package com.findmeahometeam.reskiume.ui.chats.checkChat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.findmeahometeam.reskiume.domain.model.NonHumanAnimalState
import com.findmeahometeam.reskiume.domain.model.user.User
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.backgroundColorForItems
import com.findmeahometeam.reskiume.ui.core.components.RmButton
import com.findmeahometeam.reskiume.ui.core.components.RmDialog
import com.findmeahometeam.reskiume.ui.core.components.RmDisplayAvatarOrPlaceholder
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmSecondaryText
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.RmTextField
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.debouncedClickable
import com.findmeahometeam.reskiume.ui.core.components.rmDebouncer
import com.findmeahometeam.reskiume.ui.core.primaryGreen
import com.findmeahometeam.reskiume.ui.core.primaryRed
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import com.findmeahometeam.reskiume.ui.core.textColor
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_chat_screen_about_non_human_animal
import reskiume.composeapp.generated.resources.check_chat_screen_check_all_activists_option
import reskiume.composeapp.generated.resources.check_chat_screen_decline_to_foster_button
import reskiume.composeapp.generated.resources.check_chat_screen_finish_foster_and_evaluate_button
import reskiume.composeapp.generated.resources.check_chat_screen_finish_rescue_event_and_evaluate_button
import reskiume.composeapp.generated.resources.check_chat_screen_finish_rescue_event_button
import reskiume.composeapp.generated.resources.check_chat_screen_foster_action
import reskiume.composeapp.generated.resources.check_chat_screen_foster_button
import reskiume.composeapp.generated.resources.check_chat_screen_image_content_description
import reskiume.composeapp.generated.resources.check_chat_screen_leave_chat_message
import reskiume.composeapp.generated.resources.check_chat_screen_leave_chat_option
import reskiume.composeapp.generated.resources.check_chat_screen_leave_review_chat_option
import reskiume.composeapp.generated.resources.check_chat_screen_message_after_clicking_option
import reskiume.composeapp.generated.resources.check_chat_screen_more_options_content_description
import reskiume.composeapp.generated.resources.check_chat_screen_need_to_be_rehomed
import reskiume.composeapp.generated.resources.check_chat_screen_no_button
import reskiume.composeapp.generated.resources.check_chat_screen_no_messages
import reskiume.composeapp.generated.resources.check_chat_screen_non_human_animal_name
import reskiume.composeapp.generated.resources.check_chat_screen_quantity_of_non_human_animals
import reskiume.composeapp.generated.resources.check_chat_screen_rescue_action
import reskiume.composeapp.generated.resources.check_chat_screen_rescued_non_human_animal
import reskiume.composeapp.generated.resources.check_chat_screen_send_content_description
import reskiume.composeapp.generated.resources.check_chat_screen_some_non_human_animals
import reskiume.composeapp.generated.resources.check_chat_screen_title_after_clicking_accept_option
import reskiume.composeapp.generated.resources.check_chat_screen_title_after_clicking_decline_option
import reskiume.composeapp.generated.resources.check_chat_screen_title_after_clicking_finish_option
import reskiume.composeapp.generated.resources.check_chat_screen_title_after_clicking_finish_rescue_without_activist_option
import reskiume.composeapp.generated.resources.check_chat_screen_user_left_chat_message
import reskiume.composeapp.generated.resources.check_chat_screen_write_a_message_description
import reskiume.composeapp.generated.resources.check_chat_screen_yes_button
import reskiume.composeapp.generated.resources.ic_activists
import reskiume.composeapp.generated.resources.ic_exit_group
import reskiume.composeapp.generated.resources.ic_menu
import reskiume.composeapp.generated.resources.ic_send
import kotlin.Pair
import kotlin.String

@Composable
fun CheckChatScreen(
    onBackPressed: () -> Unit,
    onCheckDetails: (isFosterHome: Boolean, id: String, chatHolderId: String, chatId: String) -> Unit,
    onCheckActivist: (uid: String) -> Unit,
    onCheckNonHumanAnimal: (nonHumanAnimalId: String, caregiverId: String) -> Unit,
    onAddReview: (allActivistsToReview: List<User>) -> Unit
) {
    val checkChatViewmodel: CheckChatViewmodel = koinViewModel<CheckChatViewmodel>()

    val uiChatDetailState: UiState<UiChatDetail> by checkChatViewmodel.uiChatDetailState.collectAsStateWithLifecycle()

    val allChatMessagesState: UiState<List<UiChatMessage>> by checkChatViewmodel.allChatMessagesState.collectAsStateWithLifecycle()

    val uiState: UiState<Unit> by checkChatViewmodel.uiState.collectAsStateWithLifecycle()

    var clickedLeave: Boolean by rememberSaveable { mutableStateOf(false) }
    var clickedCheckAllActivists: Boolean by rememberSaveable { mutableStateOf(false) }
    var displayNonHumanAnimals: Boolean by rememberSaveable { mutableStateOf(true) }
    var finishAndEvaluateClicked: Boolean by rememberSaveable { mutableStateOf(false) }
    var message: String by rememberSaveable { mutableStateOf("") }

    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    val lazyListState = remember { LazyListState() }

    Scaffold(containerColor = backgroundColor) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RmResultState(uiChatDetailState) { uiChatDetail ->

                RmScaffold(
                    onBackPressed = {
                        if (uiState !is UiState.Loading) {

                            onBackPressed()
                        }
                    },
                    titleWithDescription = {

                        DisplayChatDetail(
                            uiChatDetailState = uiChatDetailState,
                            onCheckDetails = { isFosterHome: Boolean, id: String, chatHolderId: String, chatId: String ->
                                if (uiState !is UiState.Loading) {
                                    onCheckDetails(
                                        isFosterHome,
                                        id,
                                        chatHolderId,
                                        chatId
                                    )
                                }
                            },
                            onClickNonHumanAnimals = {
                                if (uiState !is UiState.Loading) {

                                    if (uiChatDetail.allNonHumanAnimals.size == 1) {
                                        onCheckNonHumanAnimal(
                                            uiChatDetail.allNonHumanAnimals[0].id,
                                            uiChatDetail.allNonHumanAnimals[0].caregiverId
                                        )
                                    } else {
                                        displayNonHumanAnimals = !displayNonHumanAnimals
                                    }
                                }
                            }
                        )
                    },
                    topAppBarActions = {

                        if (!uiChatDetail.finished
                            && (!uiChatDetail.amIChatHolder
                                    || uiChatDetail.amIChatHolder
                                    && uiChatDetail.allActivists.isNotEmpty())
                        ) {
                            DisplayDropdownMenu(
                                amIChatHolder = uiChatDetail.amIChatHolder,
                                isAllActivistNotEmpty = uiChatDetail.allActivists.isNotEmpty(),
                                onClickLeave = {
                                    clickedLeave = true
                                },
                                onClickCheckAllActivists = {
                                    clickedCheckAllActivists = true
                                }
                            )
                            when {
                                clickedLeave -> {
                                    val leaveChatMessage = stringResource(
                                        Res.string.check_chat_screen_user_left_chat_message,
                                        uiChatDetail.myUsername
                                    )
                                    RmDialog(
                                        emoji = "🚪",
                                        title = stringResource(Res.string.check_chat_screen_leave_chat_option),
                                        message = stringResource(Res.string.check_chat_screen_leave_chat_message),
                                        allowMessage = stringResource(Res.string.check_chat_screen_yes_button),
                                        denyMessage = stringResource(Res.string.check_chat_screen_no_button),
                                        onClickAllow = {
                                            checkChatViewmodel.sendMessage(
                                                message = leaveChatMessage,
                                                isAppMessage = true
                                            ) {
                                                checkChatViewmodel.leaveTheCurrentChat(
                                                    uiChatDetail.fosterHomeId,
                                                    uiChatDetail.allNonHumanAnimals.map {
                                                        Pair(
                                                            it.id,
                                                            it.caregiverId
                                                        )
                                                    }
                                                )
                                                clickedLeave = false
                                            }
                                        },
                                        onClickDeny = {
                                            clickedLeave = false
                                        }
                                    )
                                }

                                clickedCheckAllActivists -> {

                                    DisplayCheckAllActivists(
                                        uiChatDetailState = uiChatDetailState,
                                        onClickActivist = { uid ->
                                            onCheckActivist(uid)
                                            clickedCheckAllActivists = false
                                        },
                                        onDismissRequest = {
                                            clickedCheckAllActivists = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { padding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor)
                            .padding(padding)
                            .consumeWindowInsets(padding)
                            .imePadding()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        DisplayNonHumanAnimals(
                            displayNonHumanAnimals,
                            uiChatDetailState,
                            onCheckNonHumanAnimal,
                        ) {
                            displayNonHumanAnimals = it
                        }
                        RmResultState(allChatMessagesState) { uiAllChatMessages: List<UiChatMessage> ->

                            AnimatedVisibility(uiChatDetail.amIChatHolder) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (uiChatDetail.fosterHomeId.isNotEmpty()) {

                                        DisplayFosterHomeOptions(
                                            addReview = uiChatDetail.allNonHumanAnimals.any { it.nonHumanAnimalState == NonHumanAnimalState.REHOMED }
                                                    || uiChatDetail.addReview,
                                            thereIsActivist = uiChatDetail.allActivists.isNotEmpty(),
                                            onDeclineToFoster = {
                                                checkChatViewmodel.declineFoster(
                                                    myUsername = uiChatDetail.myUsername,
                                                    fosterHomeId = uiChatDetail.fosterHomeId,
                                                    allNonHumanAnimals = uiChatDetail.allNonHumanAnimals
                                                )
                                            },
                                            onAcceptToFoster = {
                                                checkChatViewmodel.acceptFoster(
                                                    fosterHomeId = uiChatDetail.fosterHomeId,
                                                    myUsername = uiChatDetail.myUsername,
                                                    allNonHumanAnimals = uiChatDetail.allNonHumanAnimals,
                                                    nonHumanAnimalState = NonHumanAnimalState.REHOMED
                                                )
                                            },
                                            onFinishFosterAndEvaluate = {
                                                finishAndEvaluateClicked = true
                                            },
                                            onLeaveChat = {
                                                checkChatViewmodel.leaveTheDeletedChat()
                                            }
                                        )
                                    } else {
                                        DisplayRescueEventOptions(
                                            isThereAnyActivist = uiChatDetail.allActivists.isNotEmpty(),
                                            onFinishRescue = {
                                                finishAndEvaluateClicked = true
                                            }
                                        )
                                    }
                                    if (finishAndEvaluateClicked) {

                                        if (uiState !is UiState.Loading) {

                                            if (uiChatDetail.finished && uiChatDetail.addReview) {

                                                checkChatViewmodel.onClickEvaluateFromChatHolder(
                                                    fosterHomeId = uiChatDetail.fosterHomeId,
                                                    allNonHumanAnimals = uiChatDetail.allNonHumanAnimals
                                                )
                                                finishAndEvaluateClicked = false
                                            } else {
                                                DisplayNonHumanAnimalStateDialogs(
                                                    uiChatDetailState = uiChatDetailState,
                                                    onFinish = { nonHumanAnimalState ->

                                                        checkChatViewmodel.finishAndEvaluate(
                                                            fosterHomeId = uiChatDetail.fosterHomeId,
                                                            myUsername = uiChatDetail.myUsername,
                                                            allNonHumanAnimals = uiChatDetail.allNonHumanAnimals,
                                                            nonHumanAnimalState = nonHumanAnimalState,
                                                            rescueEventId = uiChatDetail.rescueEventId,
                                                            creatorId = uiChatDetail.chatHolderId,
                                                            isThereAnyActivist = uiChatDetail.allActivists.isNotEmpty()
                                                        )
                                                        finishAndEvaluateClicked = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            AnimatedVisibility(
                                !uiChatDetail.amIChatHolder
                                        && uiChatDetail.finished
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (uiChatDetail.addReview) {

                                        RmButton(stringResource(Res.string.check_chat_screen_leave_review_chat_option)) {

                                            if (uiState !is UiState.Loading) {

                                                checkChatViewmodel.onClickEvaluateFromActivist(
                                                    fosterHomeId = uiChatDetail.fosterHomeId,
                                                    rescueEventId = uiChatDetail.rescueEventId,
                                                    allNonHumanAnimals = uiChatDetail.allNonHumanAnimals
                                                )
                                            }
                                        }
                                    } else {
                                        RmButton(stringResource(Res.string.check_chat_screen_leave_chat_option)) {
                                            if (uiState !is UiState.Loading) {
                                                checkChatViewmodel.leaveTheDeletedChat()
                                            }
                                        }
                                    }
                                }
                            }
                            AnimatedVisibility(uiState !is UiState.Idle) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    RmResultState(uiState) {

                                        if (uiChatDetail.addReview) {
                                            onAddReview(uiChatDetail.allActivists)
                                        } else {
                                            onBackPressed()
                                        }
                                    }
                                }
                            }

                            if (uiAllChatMessages.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .pointerInput(displayNonHumanAnimals) {
                                            if (!displayNonHumanAnimals) {
                                                awaitEachGesture {
                                                    awaitFirstDown(
                                                        requireUnconsumed = false,
                                                        pass = PointerEventPass.Initial
                                                    )
                                                    displayNonHumanAnimals = true
                                                }
                                            }
                                        }
                                ) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    RmText(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(backgroundColorForItems)
                                            .padding(16.dp)
                                            .clip(RoundedCornerShape(15.dp)),
                                        text = stringResource(Res.string.check_chat_screen_no_messages),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                                LazyColumn(
                                    modifier = Modifier
                                        .weight(1f)
                                        .pointerInput(displayNonHumanAnimals) {
                                            if (!displayNonHumanAnimals) {
                                                awaitEachGesture {
                                                    awaitFirstDown(
                                                        requireUnconsumed = false,
                                                        pass = PointerEventPass.Initial
                                                    )
                                                    displayNonHumanAnimals = true
                                                }
                                            }
                                        },
                                    state = lazyListState
                                ) {
                                    items(
                                        items = uiAllChatMessages,
                                        key = { it.hashCode() }
                                    ) { uiChatMessage ->
                                        ChatMessageItem(
                                            modifier = Modifier.animateItem(),
                                            date = uiChatMessage.date,
                                            senderId = uiChatMessage.senderId,
                                            senderName = uiChatMessage.senderName,
                                            avatar = uiChatMessage.avatar,
                                            message = uiChatMessage.message,
                                            hour = uiChatMessage.hour,
                                            isMyMessage = uiChatMessage.isMyMessage,
                                            containerColor = if (uiChatMessage.isMyMessage) {
                                                primaryGreen
                                            } else {
                                                backgroundColorForItems
                                            },
                                            onCheckActivist = onCheckActivist
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                }
                            }

                            LaunchedEffect(key1 = keyboardHeight) {
                                lazyListState.scrollBy(keyboardHeight.toFloat())
                            }

                            // Always scroll to the latest message
                            LaunchedEffect(uiAllChatMessages.size) {
                                val itemIndex: Int =
                                    lazyListState.layoutInfo.totalItemsCount - 1
                                if (itemIndex >= 0) {

                                    val lastItem: LazyListItemInfo? =
                                        lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()
                                    lastItem?.let {
                                        lazyListState.animateScrollToItem(
                                            itemIndex,
                                            it.size + it.offset
                                        )
                                    }
                                }
                            }
                        }
                        if (!uiChatDetail.finished) {
                            Row(verticalAlignment = Alignment.CenterVertically) {

                                RmTextField(
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(max = 200.dp),
                                    text = message,
                                    label = stringResource(Res.string.check_chat_screen_write_a_message_description),
                                    onValueChange = { message = it }
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                IconButton(
                                    modifier = Modifier.padding(start = 16.dp).size(32.dp),
                                    enabled = message.isNotEmpty(),
                                    onClick = {
                                        checkChatViewmodel.sendMessage(message)
                                        message = ""
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(backgroundColorForItems),
                                        painter = painterResource(Res.drawable.ic_send),
                                        contentDescription = stringResource(Res.string.check_chat_screen_send_content_description),
                                        tint = if (message.isEmpty()) tertiaryGreen else primaryGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DisplayChatDetail(
    uiChatDetailState: UiState<UiChatDetail>,
    onCheckDetails: (isFosterHome: Boolean, id: String, chatHolderId: String, chatId: String) -> Unit,
    onClickNonHumanAnimals: () -> Unit
) = (uiChatDetailState as UiState.Success<UiChatDetail>).data.run {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .debouncedClickable {
                    onCheckDetails(
                        fosterHomeId.isNotEmpty(),
                        fosterHomeId.ifEmpty { rescueEventId },
                        chatHolderId,
                        chatId
                    )
                },
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            RmText(
                text = title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                fontSize = 18.sp,
                maxLines = 1
            )
            if (rescueEventId.isNotEmpty()
                || !amIChatHolder
                || amIChatHolder
                && allActivists.isNotEmpty()
            ) {
                val action = if (fosterHomeId.isNotEmpty()) {
                    stringResource(Res.string.check_chat_screen_foster_action)
                } else {
                    stringResource(Res.string.check_chat_screen_rescue_action)
                }
                when (allNonHumanAnimals.size) {
                    1 -> {
                        RmSecondaryText(
                            text = stringResource(
                                Res.string.check_chat_screen_non_human_animal_name,
                                action,
                                allNonHumanAnimals.first().name
                            ),
                            textAlign = TextAlign.End,
                            maxLines = 1
                        )
                    }

                    2 -> {
                        RmSecondaryText(
                            text = stringResource(
                                Res.string.check_chat_screen_non_human_animal_name,
                                action,
                                stringResource(
                                    Res.string.check_chat_screen_some_non_human_animals,
                                    allNonHumanAnimals.first().name,
                                    allNonHumanAnimals.last().name
                                )
                            ),
                            textAlign = TextAlign.End,
                            maxLines = 1
                        )
                    }

                    else -> {
                        RmSecondaryText(
                            text = stringResource(
                                Res.string.check_chat_screen_quantity_of_non_human_animals,
                                action,
                                allNonHumanAnimals.size
                            ),
                            textAlign = TextAlign.End,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))
        if (rescueEventId.isNotEmpty()
            || !amIChatHolder
            || amIChatHolder
            && allActivists.isNotEmpty()
        ) {
            DisplayNonHumanAnimalAvatars(
                Modifier.weight(0.4f),
                uiChatDetailState,
                onClickNonHumanAnimals
            )
        }
    }
}

@Composable
private fun DisplayNonHumanAnimalAvatars(
    modifier: Modifier,
    uiChatDetailState: UiState<UiChatDetail>,
    onClickNonHumanAnimals: () -> Unit
) {
    Box(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.spacedBy((-20).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            (uiChatDetailState as UiState.Success<UiChatDetail>).data.allNonHumanAnimals.forEachIndexed { index, nonHumanAnimal ->
                if (index < 3) {
                    Avatar(
                        avatar = nonHumanAnimal.imageUrl,
                        avatarName = nonHumanAnimal.name
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable {
                    onClickNonHumanAnimals()
                }
        )
    }
}

@Composable
private fun Avatar(
    avatar: String,
    avatarName: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .drawWithContent {
                drawContent()
                drawCircle(
                    color = Color.White,
                    radius = 50f,
                    style = Stroke(5f)
                )
            }
    ) {
        RmDisplayAvatarOrPlaceholder(
            avatar = avatar,
            avatarSize = 40.dp,
            contentDescription =
                stringResource(
                    Res.string.check_chat_screen_image_content_description,
                    avatarName
                ),
            onClick = onClick
        )
    }
}

@Composable
private fun DisplayDropdownMenu(
    amIChatHolder: Boolean,
    isAllActivistNotEmpty: Boolean,
    onClickLeave: () -> Unit,
    onClickCheckAllActivists: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.padding(start = 8.dp)
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(Res.drawable.ic_menu),
                contentDescription = stringResource(Res.string.check_chat_screen_more_options_content_description),
                tint = textColor
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = backgroundColorForItems
        ) {
            if (!amIChatHolder) {
                DropdownMenuItem(
                    text = { RmText(stringResource(Res.string.check_chat_screen_leave_chat_option)) },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_exit_group),
                            contentDescription = stringResource(Res.string.check_chat_screen_leave_chat_option),
                            tint = textColor,
                        )
                    },
                    onClick = rmDebouncer(
                        onClick = {
                            expanded = false
                            onClickLeave()
                        }
                    )
                )
            }
            if (isAllActivistNotEmpty) {
                DropdownMenuItem(
                    text = { RmText(stringResource(Res.string.check_chat_screen_check_all_activists_option)) },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(Res.drawable.ic_activists),
                            contentDescription = stringResource(Res.string.check_chat_screen_check_all_activists_option),
                            tint = textColor,
                        )
                    },
                    onClick = rmDebouncer(
                        onClick = {
                            expanded = false
                            onClickCheckAllActivists()
                        }
                    )
                )
            }
        }
    }
}

@Composable
private fun DisplayCheckAllActivists(
    uiChatDetailState: UiState<UiChatDetail>,
    onClickActivist: (uid: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors().copy(containerColor = backgroundColor),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RmText(
                    text = stringResource(Res.string.check_chat_screen_check_all_activists_option),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    items(
                        items = (uiChatDetailState as UiState.Success<UiChatDetail>).data.allActivists,
                        key = { it.hashCode() }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().debouncedClickable {
                                onClickActivist(it.uid)
                            },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Avatar(
                                avatar = it.image,
                                avatarName = it.username
                            ) {
                                onClickActivist(it.uid)
                            }

                            Spacer(modifier = Modifier.width(8.dp))
                            RmText(
                                text = it.username,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DisplayNonHumanAnimals(
    displayNonHumanAnimals: Boolean,
    uiChatDetailState: UiState<UiChatDetail>,
    onCheckNonHumanAnimal: (nonHumanAnimalId: String, caregiverId: String) -> Unit,
    onUpdateDisplayNonHumanAnimals: (Boolean) -> Unit
) {
    AnimatedVisibility(
        visible = !displayNonHumanAnimals,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(
                    items = (uiChatDetailState as UiState.Success).data.allNonHumanAnimals,
                    key = { it.hashCode() }
                ) { nonHumanAnimal ->

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Avatar(
                            avatar = nonHumanAnimal.imageUrl,
                            avatarName = nonHumanAnimal.name
                        ) {
                            onCheckNonHumanAnimal(
                                nonHumanAnimal.id,
                                nonHumanAnimal.caregiverId
                            )
                            onUpdateDisplayNonHumanAnimals(true)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        RmText(nonHumanAnimal.name)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
private fun DisplayFosterHomeOptions(
    addReview: Boolean,
    thereIsActivist: Boolean,
    onDeclineToFoster: () -> Unit,
    onAcceptToFoster: () -> Unit,
    onFinishFosterAndEvaluate: () -> Unit,
    onLeaveChat: () -> Unit
) {
    var displayFinishFoster: Boolean by rememberSaveable { mutableStateOf(addReview) }
    var finishFosterClicked: Boolean by rememberSaveable { mutableStateOf(false) }
    var declinedFosterClicked: Boolean by rememberSaveable { mutableStateOf(false) }
    var acceptedFosterClicked: Boolean by rememberSaveable { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (thereIsActivist) {
            if (displayFinishFoster) {
                RmButton(stringResource(Res.string.check_chat_screen_finish_foster_and_evaluate_button)) {
                    finishFosterClicked = true
                }
            } else {
                RmButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.check_chat_screen_decline_to_foster_button),
                    containerColor = primaryRed.copy(alpha = 0.5f)
                ) {
                    declinedFosterClicked = true
                }
                Spacer(modifier = Modifier.width(8.dp))
                RmButton(
                    modifier = Modifier.weight(1f),
                    text = stringResource(Res.string.check_chat_screen_foster_button)
                ) {
                    acceptedFosterClicked = true
                }
            }
        } else {
            RmButton(stringResource(Res.string.check_chat_screen_leave_chat_option)) {
                onLeaveChat()
            }
        }
    }
    if (declinedFosterClicked || acceptedFosterClicked || finishFosterClicked) {

        OptionsDialog(
            title = when {
                declinedFosterClicked -> stringResource(Res.string.check_chat_screen_title_after_clicking_decline_option)

                acceptedFosterClicked -> stringResource(Res.string.check_chat_screen_title_after_clicking_accept_option)

                else -> stringResource(
                    Res.string.check_chat_screen_title_after_clicking_finish_option,
                    stringResource(Res.string.check_chat_screen_foster_action)
                )
            },
            onClickAllow = {
                when {
                    declinedFosterClicked -> {
                        onDeclineToFoster()
                        declinedFosterClicked = false
                    }

                    acceptedFosterClicked -> {
                        displayFinishFoster = true
                        onAcceptToFoster()
                        acceptedFosterClicked = false
                    }

                    else -> {
                        onFinishFosterAndEvaluate()
                        finishFosterClicked = false
                    }
                }
            },
            onClickDeny = {
                when {
                    declinedFosterClicked -> declinedFosterClicked = false

                    acceptedFosterClicked -> acceptedFosterClicked = false

                    else -> finishFosterClicked = false
                }
            }
        )
    }
}

@Composable
private fun OptionsDialog(
    title: String,
    onClickAllow: () -> Unit,
    onClickDeny: () -> Unit
) {
    RmDialog(
        emoji = "❔",
        title = title,
        message = stringResource(Res.string.check_chat_screen_message_after_clicking_option),
        allowMessage = stringResource(Res.string.check_chat_screen_yes_button),
        denyMessage = stringResource(Res.string.check_chat_screen_no_button),
        onClickAllow = onClickAllow,
        onClickDeny = onClickDeny
    )
}

@Composable
private fun DisplayRescueEventOptions(
    isThereAnyActivist: Boolean,
    onFinishRescue: () -> Unit
) {
    var finishRescueEventClicked: Boolean by rememberSaveable { mutableStateOf(false) }

    RmButton(
        if (isThereAnyActivist) {
            stringResource(Res.string.check_chat_screen_finish_rescue_event_and_evaluate_button)
        } else {
            stringResource(Res.string.check_chat_screen_finish_rescue_event_button)
        }
    ) {
        finishRescueEventClicked = true
    }

    if (finishRescueEventClicked) {

        OptionsDialog(
            title = if (isThereAnyActivist) {
                stringResource(
                    Res.string.check_chat_screen_title_after_clicking_finish_option,
                    stringResource(Res.string.check_chat_screen_rescue_action)
                )
            } else {
                stringResource(
                    Res.string.check_chat_screen_title_after_clicking_finish_rescue_without_activist_option,
                )
            },
            onClickAllow = {
                onFinishRescue()
                finishRescueEventClicked = false
            },
            onClickDeny = {
                finishRescueEventClicked = false
            }
        )
    }
}

@Composable
private fun DisplayNonHumanAnimalStateDialogs(
    uiChatDetailState: UiState<UiChatDetail>,
    onFinish: (NonHumanAnimalState) -> Unit
) {
    val uiChatDetail = (uiChatDetailState as UiState.Success).data
    val nonHumanAnimalNames = if (uiChatDetail.allNonHumanAnimals.size == 1) {
        uiChatDetail.allNonHumanAnimals.first().name
    } else {
        stringResource(
            Res.string.check_chat_screen_some_non_human_animals,
            uiChatDetail.allNonHumanAnimals.subList(0, uiChatDetail.allNonHumanAnimals.size - 1)
                .joinToString(", ") { it.name },
            uiChatDetail.allNonHumanAnimals.last().name
        )
    }
    var displayRescuedDialog by rememberSaveable { mutableStateOf(uiChatDetail.rescueEventId.isNotEmpty()) }
    var displayRehomeDialog by rememberSaveable { mutableStateOf(uiChatDetail.fosterHomeId.isNotEmpty()) }

    when {
        displayRescuedDialog -> {
            RmDialog(
                emoji = "🦸",
                title = stringResource(
                    Res.string.check_chat_screen_about_non_human_animal,
                    nonHumanAnimalNames
                ),
                message = stringResource(
                    Res.string.check_chat_screen_rescued_non_human_animal,
                    nonHumanAnimalNames
                ),
                allowMessage = stringResource(Res.string.check_chat_screen_yes_button),
                denyMessage = stringResource(Res.string.check_chat_screen_no_button),
                onClickAllow = {
                    displayRehomeDialog = true
                    displayRescuedDialog = false
                },
                onClickDeny = {
                    onFinish(NonHumanAnimalState.NEEDS_TO_BE_REHOMED)
                    displayRescuedDialog = false
                }
            )
        }

        displayRehomeDialog -> {
            RmDialog(
                emoji = "🏠",
                title = stringResource(
                    Res.string.check_chat_screen_about_non_human_animal,
                    nonHumanAnimalNames
                ),
                message = stringResource(
                    Res.string.check_chat_screen_need_to_be_rehomed,
                    nonHumanAnimalNames
                ),
                allowMessage = stringResource(Res.string.check_chat_screen_yes_button),
                denyMessage = stringResource(Res.string.check_chat_screen_no_button),
                onClickAllow = {
                    onFinish(NonHumanAnimalState.NEEDS_TO_BE_REHOMED)
                    displayRehomeDialog = false
                },
                onClickDeny = {
                    onFinish(NonHumanAnimalState.SAVED)
                    displayRehomeDialog = false
                }
            )
        }
    }
}

@Composable
private fun ChatMessageItem(
    modifier: Modifier = Modifier,
    date: String,
    senderId: String,
    senderName: String,
    avatar: String,
    message: String,
    hour: String,
    isMyMessage: Boolean,
    containerColor: Color,
    onCheckActivist: (uid: String) -> Unit
) {
    if (date.isNotEmpty()) {
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RmText(
                text = date,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (senderId.isEmpty()) {
            RmText(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColorForItems)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(15.dp)),
                text = message,
                textAlign = TextAlign.Center
            )
        } else {
            if (!isMyMessage) {
                RmDisplayAvatarOrPlaceholder(
                    avatar = avatar,
                    contentDescription =
                        stringResource(
                            Res.string.check_chat_screen_image_content_description,
                            senderName
                        )
                ) {
                    onCheckActivist(senderId)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column(
                modifier = modifier
                    .fillMaxWidth(0.5f)
                    .clip(
                        if (isMyMessage) {
                            RoundedCornerShape(15.dp)
                        } else {
                            RoundedCornerShape(
                                topStart = 15.dp,
                                topEnd = 15.dp,
                                bottomEnd = 15.dp
                            )
                        }
                    ).background(containerColor)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    RmText(
                        text = senderName,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    RmText(
                        modifier = Modifier.fillMaxWidth(),
                        text = message
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    RmText(
                        modifier = Modifier.fillMaxWidth(),
                        text = hour,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}
