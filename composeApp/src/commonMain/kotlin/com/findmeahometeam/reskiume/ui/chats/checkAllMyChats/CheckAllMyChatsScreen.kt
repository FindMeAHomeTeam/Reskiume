package com.findmeahometeam.reskiume.ui.chats.checkAllMyChats

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.findmeahometeam.reskiume.ui.core.backgroundColor
import com.findmeahometeam.reskiume.ui.core.components.RmListAvatarType
import com.findmeahometeam.reskiume.ui.core.components.RmListItem
import com.findmeahometeam.reskiume.ui.core.components.RmResultState
import com.findmeahometeam.reskiume.ui.core.components.RmScaffold
import com.findmeahometeam.reskiume.ui.core.components.RmText
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.tertiaryGreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_my_chats_screen_no_chats
import reskiume.composeapp.generated.resources.check_all_my_chats_screen_title

@Composable
fun CheckAllMyChatsScreen(
    onCheckChat: (chatId: String, lastTimestamp: Long) -> Unit
) {
    val checkAllMyChatsViewmodel: CheckAllMyChatsViewmodel =
        koinViewModel<CheckAllMyChatsViewmodel>()

    checkAllMyChatsViewmodel.remoteSync.collectAsStateWithLifecycle()

    val uiChatListState: UiState<List<UiChat>> by checkAllMyChatsViewmodel.uiChatListState
        .collectAsStateWithLifecycle()

    RmScaffold(
        title = stringResource(Res.string.check_all_my_chats_screen_title)
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
            RmResultState(uiChatListState) { uiChatList: List<UiChat> ->

                if (uiChatList.isEmpty()) {
                    Spacer(modifier = Modifier.weight(1f))
                    RmText(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(Res.string.check_all_my_chats_screen_no_chats),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.weight(1f))
                } else {

                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(
                            items = uiChatList,
                            key = { it.hashCode() }
                        ) { uiChat ->
                            RmListItem(
                                modifier = Modifier.animateItem(),
                                title = uiChat.title,
                                titleTag = uiChat.time.takeIf { it.isNotEmpty() },
                                titleTagColor = tertiaryGreen,
                                description = uiChat.lastText.ifEmpty { "--" },
                                listAvatarType = RmListAvatarType.Image(uiChat.avatar),
                                onClick = {
                                    onCheckChat(
                                        uiChat.chat.id,
                                        uiChat.timestamp
                                    )
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
