package com.findmeahometeam.reskiume.data.database.entity.chat

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.findmeahometeam.reskiume.domain.model.chat.ActivistInfo
import com.findmeahometeam.reskiume.domain.model.chat.BlockedUserInfo
import com.findmeahometeam.reskiume.domain.model.chat.Chat
import com.findmeahometeam.reskiume.domain.model.chat.ChatMessage
import com.findmeahometeam.reskiume.domain.model.chat.NonHumanAnimalInfo

@Entity
data class ChatEntity(
    @PrimaryKey val id: String,
    val fosterHomeId: String,
    val rescueEventId: String,
    val savedBy: String,
    val chatHolderId: String,
    val myUserIsConnected: Boolean,
    val finished: Boolean
) {
    fun toDomain(
        allNonHumanAnimalsInfo: List<NonHumanAnimalInfo>,
        allActivistsInfo: List<ActivistInfo>,
        allBlockedUsersInfo: List<BlockedUserInfo>,
        allChatMessages: List<ChatMessage>
    ): Chat {
        return Chat(
            id = id,
            fosterHomeId = fosterHomeId,
            rescueEventId = rescueEventId,
            savedBy = savedBy,
            chatHolderId = chatHolderId,
            allNonHumanAnimalsInfo = allNonHumanAnimalsInfo,
            allActivistsInfo = allActivistsInfo,
            allBlockedUsersInfo = allBlockedUsersInfo,
            allChatMessages = allChatMessages,
            myUserIsConnected = myUserIsConnected,
            finished = finished
        )
    }
}

data class ChatEntityWithAllData(
    @Embedded val chatEntity: ChatEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "chatId"
    )
    val allNonHumanAnimalsInfo: List<NonHumanAnimalInfoEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "chatId"
    )
    val allActivistsInfo: List<ActivistInfoEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "chatId"
    )
    val allBlockedUsersInfo: List<BlockedUserInfoEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "chatId"
    )
    val allChatMessages: List<ChatMessageEntity>
)
