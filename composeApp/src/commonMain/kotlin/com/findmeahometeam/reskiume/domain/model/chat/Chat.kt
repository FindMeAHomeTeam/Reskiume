package com.findmeahometeam.reskiume.domain.model.chat

import com.findmeahometeam.reskiume.data.database.entity.chat.ChatEntity
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChat
import com.findmeahometeam.reskiume.data.remote.response.chat.RemoteChatMessage
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class Chat(
    val id: String,
    val fosterHomeId: String,
    val rescueEventId: String,
    val savedBy: String = "",
    val chatHolderId: String,
    val allNonHumanAnimalsInfo: List<NonHumanAnimalInfo>,
    val allActivistsInfo: List<ActivistInfo>,
    val allBlockedUsersInfo: List<BlockedUserInfo> = emptyList(),
    val allChatMessages: List<ChatMessage> = emptyList(),
    val active: Boolean
) {
    @OptIn(ExperimentalTime::class)
    private fun setId(): String =
        id.ifBlank { Clock.System.now().epochSeconds.toString() + chatHolderId }

    fun toEntity(): ChatEntity {
        return ChatEntity(
            id = id.ifBlank { setId() },
            fosterHomeId = fosterHomeId,
            rescueEventId = rescueEventId,
            savedBy = savedBy,
            chatHolderId = chatHolderId,
            active = active
        )
    }

    fun toRemoteChat(): RemoteChat {
        return RemoteChat(
            id = id.ifBlank { setId() },
            fosterHomeId = fosterHomeId,
            rescueEventId = rescueEventId,
            chatHolderId = chatHolderId,
            allNonHumanAnimalsInfo = allNonHumanAnimalsInfo.map { it.toData() },
            allActivistsInfo = allActivistsInfo.map { it.uid },
            allBlockedUsersInfo = allBlockedUsersInfo.map { it.toData() }
        )
    }

    fun toRemoteChatMessageList(): List<RemoteChatMessage> {
        return allChatMessages.map { it.toData() }
    }
}
