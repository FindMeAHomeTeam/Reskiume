package com.findmeahometeam.reskiume.data.database.entity.chat

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import androidx.room3.Relation
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
    val acceptedFoster: Boolean,
    val finished: Boolean,
    val addReview: Boolean,
    val timestamp: Long
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
            acceptedFoster = acceptedFoster,
            finished = finished,
            addReview = addReview,
            timestamp = timestamp
        )
    }
}

data class ChatEntityWithAllData(
    @Embedded val chatEntity: ChatEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["chatId"]
    )
    val allNonHumanAnimalsInfo: List<NonHumanAnimalInfoEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["chatId"]
    )
    val allActivistsInfo: List<ActivistInfoEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["chatId"]
    )
    val allBlockedUsersInfo: List<BlockedUserInfoEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["chatId"]
    )
    val allChatMessages: List<ChatMessageEntity>
)
