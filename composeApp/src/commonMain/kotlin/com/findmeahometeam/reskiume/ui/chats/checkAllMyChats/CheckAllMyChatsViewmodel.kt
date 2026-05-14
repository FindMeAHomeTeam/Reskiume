package com.findmeahometeam.reskiume.ui.chats.checkAllMyChats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findmeahometeam.reskiume.data.remote.response.AuthUser
import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.model.chat.ChatMessage
import com.findmeahometeam.reskiume.domain.usecases.authUser.ObserveAuthStateInAuthDataSource
import com.findmeahometeam.reskiume.domain.usecases.chat.GetAllMyChatsFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetAllMyChatsFromRemoteRepository
import com.findmeahometeam.reskiume.domain.usecases.chat.GetChatFromLocalRepository
import com.findmeahometeam.reskiume.domain.usecases.image.GetImagePathForFileNameFromLocalDataSource
import com.findmeahometeam.reskiume.ui.core.components.UiState
import com.findmeahometeam.reskiume.ui.core.components.toUiState
import com.findmeahometeam.reskiume.ui.fosterHomes.checkFosterHome.CheckFosterHomeUtil
import com.findmeahometeam.reskiume.ui.rescueEvents.checkRescueEvent.CheckRescueEventUtil
import com.findmeahometeam.reskiume.ui.util.StringProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import reskiume.composeapp.generated.resources.Res
import reskiume.composeapp.generated.resources.check_all_my_chats_screen_chat_message_sent_days_ago_timestamp
import reskiume.composeapp.generated.resources.check_all_my_chats_screen_chat_message_sent_hours_ago_timestamp
import reskiume.composeapp.generated.resources.check_all_my_chats_screen_chat_message_sent_just_now_timestamp
import reskiume.composeapp.generated.resources.check_all_my_chats_screen_chat_message_sent_minutes_ago_timestamp
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class CheckAllMyChatsViewmodel(
    observeAuthStateInAuthDataSource: ObserveAuthStateInAuthDataSource,
    private val getAllMyChatsFromLocalRepository: GetAllMyChatsFromLocalRepository,
    private val getAllMyChatsFromRemoteRepository: GetAllMyChatsFromRemoteRepository,
    private val getChatFromLocalRepository: GetChatFromLocalRepository,
    private val manageChatUtil: ManageChatUtil,
    private val checkFosterHomeUtil: CheckFosterHomeUtil,
    private val checkRescueEventUtil: CheckRescueEventUtil,
    private val getImagePathForFileNameFromLocalDataSource: GetImagePathForFileNameFromLocalDataSource,
    private val getStringProvider: StringProvider
) : ViewModel() {

    // The sync stream to write to room database
    @OptIn(ExperimentalCoroutinesApi::class)
    val remoteSync: StateFlow<Unit> =
        observeAuthStateInAuthDataSource()
            .flatMapConcat { authUser: AuthUser? ->
                val myUid = authUser?.uid ?: return@flatMapConcat flowOf(Unit)

                val lastChatTimestamp =
                    getAllMyChatsFromLocalRepository(myUid).first().maxOfOrNull { it.timestamp }
                        ?: 0L

                getAllMyChatsFromRemoteRepository(
                    myUid,
                    lastChatTimestamp
                ).map { allChats ->

                    allChats.forEach { chat ->
                        val localChat: Chat? = getChatFromLocalRepository(
                            chat.id
                        ).firstOrNull()

                        if (localChat == null) {
                            manageChatUtil.insertChatInLocalRepo(
                                chat = chat,
                                myUid = myUid
                            )
                        } else {
                            manageChatUtil.modifyChatInLocalRepo(
                                updatedChat = chat,
                                previousChat = localChat,
                                myUid = myUid
                            )
                        }
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5000),
                initialValue = Unit
            )

    // The read stream from room database
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiChatListState: StateFlow<UiState<List<UiChat>>> =
        observeAuthStateInAuthDataSource()
            .flatMapConcat { authUser: AuthUser? ->

                val myUid = authUser?.uid ?: return@flatMapConcat flowOf()
                getAllMyChatsFromLocalRepository(myUid).map { list ->
                    list.map { chat ->

                        var avatar: String
                        var title: String

                        if (chat.fosterHomeId.isNotEmpty()) {

                            val fosterHome = checkFosterHomeUtil.getFosterHomeFlow(
                                chat.fosterHomeId,
                                chat.chatHolderId,
                                viewModelScope
                            ).firstOrNull()

                            avatar = fosterHome?.imageUrl ?: ""
                            title = fosterHome?.title ?: ""
                        } else {

                            val rescueEvent = checkRescueEventUtil.getRescueEventFlow(
                                chat.rescueEventId,
                                chat.chatHolderId,
                                viewModelScope
                            ).firstOrNull()

                            avatar = rescueEvent?.imageUrl ?: ""
                            title = rescueEvent?.title ?: ""
                        }
                        val lastMessage: ChatMessage? =
                            chat.allChatMessages.maxByOrNull { it.timestamp }

                        UiChat(
                            chat = chat,
                            avatar = if (avatar.isEmpty()) {
                                avatar
                            } else {
                                getImagePathForFileNameFromLocalDataSource(avatar)
                            },
                            title = title,
                            time = lastMessage?.timestamp?.millisecondsToDuration() ?: "",
                            lastText = lastMessage?.message ?: "",
                            timestamp = lastMessage?.timestamp ?: 0
                        )
                    }.sortedBy { uiChat -> uiChat.timestamp }
                }
            }
            .toUiState()
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5000),
                initialValue = UiState.Loading()
            )

    // Transform a timestamp into a more readable format.
    @OptIn(ExperimentalTime::class)
    private suspend fun Long.millisecondsToDuration(): String {
        val now = Clock.System.now().toEpochMilliseconds()
        val differenceMs = now - this

        val seconds = differenceMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days >= 7 -> {
                val instant = Instant.fromEpochMilliseconds(this)
                val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

                val dd = dateTime.day.toString().padStart(2, '0')
                val mm = dateTime.month.number.toString().padStart(2, '0')
                val yy = dateTime.year.toString().takeLast(2)

                "$dd/$mm/$yy"
            }

            days > 0 -> {
                getStringProvider.getStringResource(
                    Res.string.check_all_my_chats_screen_chat_message_sent_days_ago_timestamp,
                    days
                )
            }

            hours > 0 -> {
                getStringProvider.getStringResource(
                    Res.string.check_all_my_chats_screen_chat_message_sent_hours_ago_timestamp,
                    hours
                )
            }

            minutes > 0 -> {
                getStringProvider.getStringResource(
                    Res.string.check_all_my_chats_screen_chat_message_sent_minutes_ago_timestamp,
                    minutes
                )
            }

            else -> {
                getStringProvider.getStringResource(Res.string.check_all_my_chats_screen_chat_message_sent_just_now_timestamp)
            }
        }
    }
}

data class UiChat(
    val chat: Chat,
    val avatar: String,
    val title: String,
    val time: String,
    val lastText: String,
    val timestamp: Long
)
